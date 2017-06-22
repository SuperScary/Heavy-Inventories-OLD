package superscary.heavyinventories.server.player;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import superscary.heavyinventories.HeavyInventories;
import superscary.heavyinventories.calc.IWeighable;
import superscary.heavyinventories.calc.PlayerWeightCalculator;
import superscary.heavyinventories.server.config.WeightsConfig;
import superscary.heavyinventories.server.player.network.PlayerUpdate;
import superscary.heavyinventories.server.player.network.PlayerUpdateRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2017 by SuperScary(ERBF) http://codesynced.com
 * <p>
 * All rights reserved. No part of this software may be reproduced,
 * distributed, or transmitted in any form or by any means, including
 * photocopying, recording, or other electronic or mechanical methods,
 * without the prior written permission of the publisher, except in
 * the case of brief quotations embodied in critical reviews and
 * certain other noncommercial uses permitted by copyright law.
 */
public class WeighablePlayer implements IWeighable, ICapabilityProvider
{

	private static final String KEY = "weighableplayer";

	private Variable<Double> weight, maxWeight, stamina, maxStamina, speed, maxSpeed;
	private EntityPlayer player;
	private Map<String, Variable<?>> map;

	public static WeighablePlayer get(EntityPlayer player)
	{
		return player != null ? player.getCapability(WEIGHABLE_CAPABILITY, null) : getDefault();
	}

	public static void reset(EntityPlayer player)
	{
		get(player).copy(getDefault());
	}

	public static WeighablePlayer getDefault()
	{
		return new WeighablePlayer(Minecraft.getMinecraft().player);
	}

	public WeighablePlayer()
	{
		this.map = new HashMap<>();
		initializeValues();
	}

	public WeighablePlayer(EntityPlayer player)
	{
		this();
		this.player = player;
	}

	public void loadNBTData(NBTTagCompound compound)
	{
		if (compound == null || !compound.hasKey(KEY)) return;

		NBTTagCompound properties = compound.getCompoundTag(KEY);
		if (properties == null) return;

		weight.variable = properties.getDouble("weight");
		speed.variable = properties.getDouble("speed");
		stamina.variable = properties.getDouble("stamina");
		maxWeight.variable = properties.getDouble("maxWeight");
		maxSpeed.variable = properties.getDouble("maxSpeed");
		maxStamina.variable = properties.getDouble("maxStamina");
	}

	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setDouble("weight", getWeight());
		tagCompound.setDouble("maxWeight", getMaxWeight());
		tagCompound.setDouble("stamina", getStamina());
		tagCompound.setDouble("maxStamina", getMaxStamina());
		tagCompound.setDouble("speed", getSpeed());
		tagCompound.setDouble("maxSpeed", getMaxSpeed());
	}

	public void sendSynchronization(boolean all)
	{
		HeavyInventories.getNetwork().sendTo(new PlayerUpdate(this, all), (EntityPlayerMP) player);
	}

	@SideOnly(Side.CLIENT)
	public void requestSynchronization(boolean all)
	{
		Minecraft.getMinecraft()
				 .addScheduledTask(() -> HeavyInventories.networkWrapper.sendToServer(new PlayerUpdateRequest(all)));
	}

	public void update()
	{
		if (!player.world.isRemote)
		{
			sendSynchronization(false);
		}
		else
		{
			if (WeightsConfig.isEnabled)
			{
				if (!player.capabilities.isCreativeMode || WeightsConfig.allowInCreative)
				{
					if (Minecraft.getMinecraft().currentScreen == null)
					{
						if (isOverEncumbered())
						{
							player.motionX *= 0;
							player.motionZ *= 0;
						}
						else if (isEncumbered())
						{
							player.motionX *= WeightsConfig.encumberedSpeed;
							player.motionY *= WeightsConfig.encumberedSpeed;
							player.motionZ *= WeightsConfig.encumberedSpeed;
						}
					}
				}
			}
		}
	}

	@Override
	public double getWeight()
	{
		return this.weight.get();
	}

	public double getMaxWeight()
	{
		return this.maxWeight.get();
	}

	public boolean isEncumbered()
	{
		return getRelativeWeight() >= 0.85;
	}

	public boolean isOverEncumbered()
	{
		return getRelativeWeight() >= 1.0;
	}

	public double getRelativeWeight()
	{
		return getWeight() / getMaxWeight();
	}

	public double getStamina()
	{
		return stamina.get();
	}

	public double getMaxStamina()
	{
		return maxStamina.get();
	}

	public double getSpeed()
	{
		return speed.get();
	}

	public double getMaxSpeed()
	{
		return maxSpeed.get();
	}

	public Map<String, Variable<?>> getStats()
	{
		return map;
	}

	public void updateWeight()
	{
		if (!player.world.isRemote)
		{
			weight.set(PlayerWeightCalculator.calculateWeight(player));
			sendSynchronization(false);
		}
	}

	public void copy(WeighablePlayer props)
	{
		this.weight = props.weight;
		this.maxWeight = props.maxWeight;
	}

	public void initializeValues()
	{
		this.maxWeight = registerValue("maxWeight", 350D);
		this.weight = registerValue("weight", 0D);
		this.maxStamina = registerValue("maxStamina", 0D);
		this.stamina = registerValue("stamina", 0D);
		this.maxSpeed = registerValue("maxSpeed", 0D);
		this.speed = registerValue("speed", 0D);
	}

	private <T> Variable<T> registerValue(String name, T in)
	{
		Variable<T> var = new Variable<>(in);
		map.put(name, var);
		return var;
	}

	private static final Capability.IStorage<WeighablePlayer> storage = new Capability.IStorage<WeighablePlayer>()
	{

		@Override
		public NBTBase writeNBT(Capability<WeighablePlayer> capability, WeighablePlayer instance, EnumFacing side)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			instance.saveNBTData(tagCompound);
			return null;
		}

		@Override
		public void readNBT(Capability<WeighablePlayer> capability, WeighablePlayer instance, EnumFacing side,
				NBTBase nbt)
		{
			instance.loadNBTData((NBTTagCompound) nbt);
		}
	};

	@CapabilityInject(WeighablePlayer.class)
	public static Capability<WeighablePlayer> WEIGHABLE_CAPABILITY = null;

	public static void init()
	{
		CapabilityManager.INSTANCE.register(WeighablePlayer.class, storage, WeighablePlayer.class);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == WEIGHABLE_CAPABILITY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == WEIGHABLE_CAPABILITY ? (T) this : null;
	}

	public class Variable<E>
	{
		private Object variable;
		private boolean changed;

		public Variable(E variable)
		{
			this.variable = variable;
			this.changed = false;
		}

		public void set(Object variable)
		{
			if (!this.variable.equals(variable) && this.variable.getClass() == variable.getClass())
			{
				this.changed = true;
				this.variable = variable;
			}
		}

		@SuppressWarnings("unchecked")
		public E get()
		{
			return (E) variable;
		}

		public boolean isChanged()
		{
			return changed;
		}

		public void setSynced()
		{
			this.changed = false;
		}

		public void writeConditionally(ByteBuf buf)
		{
			buf.writeBoolean(changed);
			if (changed)
				write(buf);
		}

		public void write(ByteBuf buf)
		{
			Class<?> c = variable.getClass();
			if (c == Integer.class) {
				buf.writeInt((int) variable);
			}
			else if (c == Boolean.class) {
				buf.writeBoolean((boolean) variable);
			}
			else if (c == Byte.class) {
				buf.writeByte((byte) variable);
			}
			else if (c == Short.class) {
				buf.writeShort((short) variable);
			}
			else if (c == Long.class) {
				buf.writeLong((long) variable);
			}
			else if (c == Character.class) {
				buf.writeChar((char) variable);
			}
			else if (c == Double.class) {
				buf.writeDouble((double) variable);
			}
			else if (c == Float.class) {
				buf.writeFloat((float) variable);
			}
			else {

			}
			setSynced();
		}

		public void read(ByteBuf buf)
		{
			Class<?> c = variable.getClass();
			if (c == Integer.class) {
				variable = buf.readInt();
			}
			else if (c == Boolean.class) {
				variable = buf.readBoolean();
			}
			else if (c == Byte.class) {
				variable = buf.readByte();
			}
			else if (c == Short.class) {
				variable = buf.readShort();
			}
			else if (c == Long.class) {
				variable = buf.readLong();
			}
			else if (c == Character.class) {
				variable = buf.readChar();
			}
			else if (c == Double.class) {
				variable = buf.readDouble();
			}
			else if (c == Float.class) {
				variable = buf.readFloat();
			}
			else {

			}
		}

		public void readConditionally(ByteBuf buf)
		{
			changed = buf.readBoolean();
			if (changed) {
				read(buf);
			}
		}

	}

}

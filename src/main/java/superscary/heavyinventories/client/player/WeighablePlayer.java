package superscary.heavyinventories.client.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import superscary.heavyinventories.calc.IWeighable;
import superscary.heavyinventories.calc.PlayerWeightCalculator;

import javax.annotation.Nullable;

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
public class WeighablePlayer extends EntityPlayer implements IWeighable
{

	private double weight;
	private double maxWeight;
	private EntityPlayer player;

	public WeighablePlayer(EntityPlayer player)
	{
		super(player.world, player.getGameProfile());
		this.player = player;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		weight = compound.getDouble("weight");

		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setDouble("weight", getWeight());

		super.writeEntityToNBT(compound);
	}

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

	@Override
	public boolean isSpectator()
	{
		return player.isSpectator();
	}

	@Override
	public boolean isCreative()
	{
		return player.isCreative();
	}

	@Override
	public double getWeight()
	{
		return this.weight;
	}

	public double getMaxWeight()
	{
		return this.maxWeight;
	}

	public boolean isEncumbered()
	{
		return getWeight() < getMaxWeight();
	}

	public void updateWeight()
	{
		if (!player.world.isRemote)
		{
			weight = PlayerWeightCalculator.calculateWeight(this);
		}
	}

	public void copy(WeighablePlayer props)
	{
		this.weight = props.weight;
		this.maxWeight = props.maxWeight;
	}

	private static final Capability.IStorage<WeighablePlayer> storage = new Capability.IStorage<WeighablePlayer>()
	{
		@Nullable
		@Override
		public NBTBase writeNBT(Capability<WeighablePlayer> capability, WeighablePlayer instance, EnumFacing side)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			instance.writeEntityToNBT(tagCompound);
			return null;
		}

		@Override
		public void readNBT(Capability<WeighablePlayer> capability, WeighablePlayer instance, EnumFacing side,
				NBTBase nbt)
		{
			instance.readFromNBT((NBTTagCompound) nbt);
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

}

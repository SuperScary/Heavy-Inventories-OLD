package superscary.heavyinventories.server.player.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import superscary.heavyinventories.client.ClientProxy;
import superscary.heavyinventories.server.player.WeighablePlayer;

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
public class PlayerUpdate implements IMessage
{

	private WeighablePlayer player;
	private boolean all;
	private NBTTagCompound nbtTagCompound;

	public PlayerUpdate()
	{}

	public PlayerUpdate(WeighablePlayer player, boolean all)
	{
		this.player = player;
		this.all = all;
		nbtTagCompound = new NBTTagCompound();
		if (all) player.saveNBTData(nbtTagCompound);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		all = buf.readBoolean();
		player = WeighablePlayer.getDefault();
		if (all)
		{
			nbtTagCompound = ByteBufUtils.readTag(buf);
		}
		else
		{
			for (WeighablePlayer.Variable<?> variable : player.getStats().values())
			{
				variable.readConditionally(buf);
			}
		}
		nbtTagCompound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(all);
		if (all)
		{
			ByteBufUtils.writeTag(buf, nbtTagCompound);
		}
		else
		{
			for (WeighablePlayer.Variable<?> variable : player.getStats().values())
			{
				variable.writeConditionally(buf);
			}
		}
	}

	private void store(WeighablePlayer player)
	{
		if (all)
		{
			player.loadNBTData(nbtTagCompound);
		}
		else player.copy(this.player);
	}

	public static class Handler implements IMessageHandler<PlayerUpdate, IMessage>
	{
		@Override
		public IMessage onMessage(PlayerUpdate message, MessageContext context)
		{
			if (context.side.isClient())
			{
				message.store(WeighablePlayer.get(ClientProxy.getPlayerFromContext(context)));
			}

			return null;
		}
	}

}

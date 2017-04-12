package superscary.heavyinventories.server.player.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
public class PlayerUpdateRequest implements IMessage
{

	private boolean all;

	public PlayerUpdateRequest()
	{}

	public PlayerUpdateRequest(boolean all)
	{
		this.all = all;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		all = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(all);
	}

	public static class Handler implements IMessageHandler<PlayerUpdateRequest, PlayerUpdate>
	{
		@Override
		public PlayerUpdate onMessage(PlayerUpdateRequest message, MessageContext context)
		{
			if (context.side.isServer())
			{
				EntityPlayerMP playerMP = context.getServerHandler().playerEntity;
				return new PlayerUpdate(WeighablePlayer.get(playerMP), message.all);
			}
			return null;
		}
	}

}

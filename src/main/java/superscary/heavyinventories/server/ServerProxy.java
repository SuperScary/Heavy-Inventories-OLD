package superscary.heavyinventories.server;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import superscary.heavyinventories.server.player.PlayerCommonEventHandler;
import superscary.heavyinventories.server.player.WeighablePlayer;
import superscary.heavyinventories.server.player.network.PlayerUpdate;
import superscary.heavyinventories.server.player.network.PlayerUpdateRequest;
import superscary.supercore.proxy.IProxy;

import static superscary.heavyinventories.HeavyInventories.networkWrapper;
import static superscary.heavyinventories.util.Constants.MODID;

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
public class ServerProxy implements IProxy
{

	@Override
	public void preInit()
	{
		PlayerCommonEventHandler.init();
	}

	@Override
	public void init()
	{
		WeighablePlayer.init();
	}

	@Override
	public void postInit()
	{

	}

	@Mod.EventHandler
	private void setupNetwork(FMLPreInitializationEvent event)
	{
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		int id = 0;

		networkWrapper.registerMessage(PlayerUpdateRequest.Handler.class, PlayerUpdateRequest.class, id++, Side
				.SERVER);
		networkWrapper.registerMessage(PlayerUpdate.Handler.class, PlayerUpdate.class, id++, Side.CLIENT);
	}

}

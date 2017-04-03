package superscary.heavyinventories.client;

import net.minecraftforge.common.MinecraftForge;
import superscary.heavyinventories.client.player.PlayerCommonEventHandler;
import superscary.supercore.proxy.IProxy;

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
public class ClientProxy implements IProxy
{

	@Override
	public void preInit()
	{
		ClientEventHandler clientEventHandler = new ClientEventHandler();
		clientEventHandler.register();

		MinecraftForge.EVENT_BUS.register(new PlayerCommonEventHandler());
	}

	@Override
	public void init()
	{

	}

	@Override
	public void postInit()
	{

	}

}

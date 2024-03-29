package superscary.heavyinventories.server;

import superscary.heavyinventories.client.player.WeighablePlayer;
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
public class ServerProxy implements IProxy
{

	@Override
	public void preInit()
	{

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

}

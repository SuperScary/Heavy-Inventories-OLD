package superscary.heavyinventories.calc;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import superscary.heavyinventories.client.player.WeighablePlayer;
import superscary.heavyinventories.server.config.WeightsConfig;

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
public class PlayerWeightCalculator
{

	public static double calculateWeight(WeighablePlayer player)
	{
		double weight = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack != null)
			{
				weight += getWeight(stack);
			}
		}
		return weight;
	}

	public static double getWeight(ItemStack stack)
	{
		return WeightsConfig.getConfig().get(Configuration.CATEGORY_GENERAL, stack.getItem().getUnlocalizedName().substring(5), 0.5).getDouble();
	}

}

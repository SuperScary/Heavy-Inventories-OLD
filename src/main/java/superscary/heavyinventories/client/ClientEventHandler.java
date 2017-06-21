package superscary.heavyinventories.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import superscary.heavyinventories.calc.PlayerWeightCalculator;
import superscary.heavyinventories.server.config.WeightsConfig;
import superscary.heavyinventories.server.player.WeighablePlayer;
import superscary.supercore.tools.EnumColor;

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
class ClientEventHandler
{

	public void register()
	{
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new WeightsConfig());
	}

	@SubscribeEvent
	public void mouseOverTooltip(ItemTooltipEvent event)
	{
		// TODO localize
		if (WeightsConfig.isEnabled) {
			ItemStack stack = event.getItemStack();
			double weight = PlayerWeightCalculator.getWeight(stack);
			event.getToolTip().add(ChatFormatting.BOLD + "" + ChatFormatting.WHITE + "Weight: " + weight);
			if (stack.getCount() > 1) {
				event.getToolTip().add(I18n.format("hi.gui.weight") + " " + (weight * stack.getCount()));
			}
			else if (Minecraft.getMinecraft().currentScreen != null)
			{
				if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				{
					event.getToolTip()
						 .add(I18n.format("hi.gui.maxStackWeight", stack.getMaxStackSize()) + " " + (weight * stack
								 .getMaxStackSize()));
				}
				else
				{
					event.getToolTip().add(I18n.format("hi.gui.shift", EnumColor.YELLOW + "SHIFT" + EnumColor.GREY));
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if (player != null)
			{
				if (player.world.isRemote)
				{
					WeighablePlayer.get(player).requestSynchronization(true);
				}
			}
		}
	}

}

package superscary.heavyinventories.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import superscary.heavyinventories.calc.PlayerWeightCalculator;
import superscary.heavyinventories.server.player.WeighablePlayer;
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
				event.getToolTip().add("Stack Weight: " + (weight * stack.getCount()));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if (event.getEntity().world.isRemote)
			{
				WeighablePlayer.get(player).requestSynchronization(true);
			}
		}
	}

	public Vec3d getPositionEyes(EntityPlayer player, float partialTick)
	{
		if (partialTick == 1.0F) {

			return new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		}
		else {
			double d0 = player.prevPosX + (player.posX - player.prevPosX) * partialTick;
			double d1 = player.prevPosY + (player.posY - player.prevPosY) * partialTick + player.getEyeHeight();
			double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTick;
			return new Vec3d(d0, d1, d2);
		}
	}

}

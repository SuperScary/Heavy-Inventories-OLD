package superscary.heavyinventories.server.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import superscary.heavyinventories.server.config.WeightsConfig;

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

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class PlayerCommonEventHandler
{

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new PlayerCommonEventHandler());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if (event.phase.equals(TickEvent.Phase.END))
		{
			WeighablePlayer.get(event.player).update();
		}
	}

	@SubscribeEvent
	public void onPlayerJump(LivingEvent.LivingJumpEvent event)
	{
		if (event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) event.getEntityLiving();
			if (WeightsConfig.isEnabled && (!p.capabilities.isCreativeMode || WeightsConfig.allowInCreative)
					&& WeighablePlayer.get(p).isEncumbered())
			{
				p.motionY *= 0;
				if (p.world.isRemote)
					p.sendMessage(new TextComponentTranslation("hi.splash.noJump"));
			}
		}
	}

	// TODO add events for inventory changes --> recalculate weight
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDestroyItem(PlayerDestroyItemEvent event)
	{
		updateWeight(event.getEntityPlayer());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onTossItem(ItemTossEvent event)
	{
		updateWeight(event.getPlayer());
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event)
	{
		updateWeight(event.getEntityPlayer());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onArrowLoose(ArrowLooseEvent event)
	{
		updateWeight(event.getEntityPlayer());
	}

	private void updateWeight(EntityPlayer player)
	{
		WeighablePlayer.get(player).updateWeight();
	}

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent.Entity event)
	{
		if (!(event.getEntity() instanceof EntityPlayer))
		{ return; }
		event.addCapability(new ResourceLocation(MODID + ":" + MODID),
				new WeighablePlayer((EntityPlayer) event.getEntity()));
	}

}

package superscary.heavyinventories.server.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.List;

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
public class WeightsConfig
{

	public static Configuration config;
	public static final String GENERAL = Configuration.CATEGORY_GENERAL;

	public static boolean isEnabled;
	public static boolean allowInCreative;
	public static double encumberedSpeed;

	public static void init(File file)
	{
		File configFile = new File(file, MODID + "_weights.cfg");
		if (config == null)
		{
			config = new Configuration(configFile);
		}
		loadConfig();
	}

	private static void loadConfig()
	{
		config.load();

		isEnabled = config.getBoolean("enabled", "Setup", false, "Enables the weight system");
		allowInCreative = config.getBoolean("allowInCreative", "Setup", false, "Allow weight tracking while in creative mode");
		encumberedSpeed = config.getFloat("encumberedSpeed", "Setup", (float) 0.2, 0, 1, "The speed factor the player will be slowed down.");

		List<Item> itemList = ImmutableList.copyOf(Item.REGISTRY);
		for (Item item : itemList)
		{
			config.getFloat(item.getUnlocalizedName().substring(5), GENERAL, generateWeight(item), 0, 16,
					"Sets the carry weight of item " + item.getUnlocalizedName().substring(5, item.getUnlocalizedName()
																								  .length()));
		}

		List<Block> blockList = ImmutableList.copyOf(Block.REGISTRY);
		for (Block block : blockList)
		{
			config.getFloat(block.getUnlocalizedName().substring(5, block.getUnlocalizedName().length()), GENERAL,
					generateWeight(block), 0, 16, "Sets the carry weight of block " + block.getUnlocalizedName()
																						   .substring(5, block.getUnlocalizedName().length()));
		}

		config.save();

	}

	private static float generateWeight(Item item)
	{

		if (item instanceof ItemTool)
		{
			switch (((ItemTool) item).getToolMaterial())
			{
				case WOOD:
					return 2;
				case STONE:
					return 5;
				case IRON:
					return 8;
				case GOLD:
					return 6;
				case DIAMOND:
					return 7;
			}
		}

		if (item instanceof ItemBlock) return generateWeight(((ItemBlock) item).block);
		if (item instanceof ItemArmor)
		{
			switch (((ItemArmor) item).getArmorMaterial())
			{
				case LEATHER:
					return 3;
				case CHAIN:
					return 5;
				case DIAMOND:
					return 7;
				case GOLD:
					return 5;
				case IRON:
					return 8;
			}
		}
		return 2;
	}

	private static float generateWeight(Block block)
	{
		Material m = block.getMaterial(block.getDefaultState());
		if (m == Material.AIR || m == Material.BARRIER || m == Material.FIRE || m == Material.PORTAL) return 0;
		if (m == Material.CACTUS || m == Material.GRASS || m == Material.GOURD || m == Material.CRAFTED_SNOW
				|| m == Material.LEAVES || m == Material.PLANTS || m == Material.GLASS || m == Material.VINE
				|| m == Material.WEB) return 0.1f;
		if (m == Material.ANVIL || m == Material.DRAGON_EGG || m == Material.IRON) return 8;
		if (m == Material.CAKE || m == Material.CARPET || m == Material.TNT || m == Material.CLOTH
				|| m == Material.SPONGE) return 0.5f;
		if (m == Material.CIRCUITS || m == Material.SAND || m == Material.REDSTONE_LIGHT) return 1;
		if (m == Material.CORAL || m == Material.LAVA || m == Material.ICE || m == Material.PACKED_ICE
				|| m == Material.PISTON) return 3.5f;
		if (m == Material.ROCK) return 5;
		// default
		return 2;
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(MODID))
		{
			syncConfig(config);
		}
	}

	private void syncConfig(Configuration config)
	{
		loadConfig();
		if (config.hasChanged())
		{
			config.save();
		}
	}

	public static Configuration getConfig()
	{
		return config;
	}

}

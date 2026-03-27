/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.world.item.EthericGemItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static moriyashiine.strawberrylib.api.module.SLibRegistries.registerItem;

public class ModItems {
	public static final Item ETHERIC_GEM = registerItem("etheric_gem", EthericGemItem::new, new Item.Properties().stacksTo(1));

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.insertAfter(Items.LEAD, ETHERIC_GEM));
	}
}

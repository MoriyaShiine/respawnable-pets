/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.item.EthericGemItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

import static moriyashiine.strawberrylib.api.module.SLibRegistries.registerItem;

public class ModItems {
	public static final Item ETHERIC_GEM = registerItem("etheric_gem", EthericGemItem::new, new Item.Settings().maxCount(1));

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.addAfter(Items.LEAD, ETHERIC_GEM));
	}
}

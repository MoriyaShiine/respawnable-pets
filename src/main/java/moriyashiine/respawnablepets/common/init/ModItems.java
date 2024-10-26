/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.item.EthericGemItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class ModItems {
	public static final Item ETHERIC_GEM = register("etheric_gem", EthericGemItem::new, new Item.Settings().maxCount(1));

	private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
		return Items.register(RegistryKey.of(RegistryKeys.ITEM, RespawnablePets.id(name)), factory, settings);
	}

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.addAfter(Items.LEAD, ETHERIC_GEM));
	}
}

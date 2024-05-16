/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.item.EthericGemItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class ModItems {
	public static final Item ETHERIC_GEM = new EthericGemItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1));

	public static void init() {
		Registry.register(Registries.ITEM, RespawnablePets.id("etheric_gem"), ETHERIC_GEM);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.addAfter(Items.LEAD, ETHERIC_GEM));
	}
}

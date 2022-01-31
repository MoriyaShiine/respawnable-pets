/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.registry;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.item.EthericGemItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ModItems {
	public static final Item ETHERIC_GEM = new EthericGemItem(new FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE).maxCount(1));

	public static void init() {
		Registry.register(Registry.ITEM, new Identifier(RespawnablePets.MOD_ID, "etheric_gem"), ETHERIC_GEM);
	}
}

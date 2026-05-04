/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.datagen.provider;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				shaped(RecipeCategory.TOOLS, ModItems.ETHERIC_GEM).define('N', ConventionalItemTags.GOLD_NUGGETS).define('E', ConventionalItemTags.ENDER_PEARLS).pattern("N N").pattern("NEN").pattern(" N ").unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL)).save(output);
			}
		};
	}

	@Override
	public String getName() {
		return RespawnablePets.MOD_ID + "_recipes";
	}
}

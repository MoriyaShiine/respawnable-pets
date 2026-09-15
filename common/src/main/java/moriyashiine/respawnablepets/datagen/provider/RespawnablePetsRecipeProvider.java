package moriyashiine.respawnablepets.datagen.provider;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.init.RespawnablePetsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class RespawnablePetsRecipeProvider extends FabricRecipeProvider {
	public RespawnablePetsRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
		return new RecipeProvider(recipes, advancements) {
			@Override
			public void buildRecipes() {
				shaped(RecipeCategory.TOOLS, RespawnablePetsItems.ETHERIC_GEM).define('N', ConventionalItemTags.GOLD_NUGGETS).define('E', ConventionalItemTags.ENDER_PEARLS).pattern("N N").pattern("NEN").pattern(" N ").unlockedBy("has_ender_pearl", has(ConventionalItemTags.ENDER_PEARLS)).save(output);
			}
		};
	}

	@Override
	public String getName() {
		return RespawnablePets.MOD_ID + "_recipes";
	}
}

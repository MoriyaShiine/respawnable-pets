package moriyashiine.respawnablepets.datagen.provider;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.init.RespawnablePetsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class RespawnablePetsRecipeProvider extends FabricRecipeProvider {
	public RespawnablePetsRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
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

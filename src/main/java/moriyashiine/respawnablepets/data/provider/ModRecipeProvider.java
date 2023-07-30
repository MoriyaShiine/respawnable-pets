package moriyashiine.respawnablepets.data.provider;

import moriyashiine.respawnablepets.common.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generate(Consumer<RecipeJsonProvider> exporter) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.ETHERIC_GEM).input('N', Items.GOLD_NUGGET).input('E', Items.ENDER_PEARL).pattern("N N").pattern("NEN").pattern(" N ").criterion("has_ender_pearl", VanillaRecipeProvider.conditionsFromItem(Items.ENDER_PEARL)).offerTo(exporter);
	}
}

package moriyashiine.respawnablepets.datagen.provider;

import moriyashiine.respawnablepets.common.init.RespawnablePetsItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class RespawnablePetsModelProvider extends FabricModelProvider {
	public RespawnablePetsModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
		generators.generateFlatItem(RespawnablePetsItems.ETHERIC_GEM, ModelTemplates.FLAT_ITEM);
	}
}

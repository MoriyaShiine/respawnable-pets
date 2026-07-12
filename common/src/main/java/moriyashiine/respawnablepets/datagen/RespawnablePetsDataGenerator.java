package moriyashiine.respawnablepets.datagen;

import moriyashiine.respawnablepets.datagen.provider.RespawnablePetsAdvancementProvider;
import moriyashiine.respawnablepets.datagen.provider.RespawnablePetsModelProvider;
import moriyashiine.respawnablepets.datagen.provider.RespawnablePetsRecipeProvider;
import moriyashiine.respawnablepets.datagen.provider.RespawnablePetsSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RespawnablePetsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(RespawnablePetsAdvancementProvider::new);
		pack.addProvider(RespawnablePetsModelProvider::new);
		pack.addProvider(RespawnablePetsRecipeProvider::new);
		pack.addProvider(RespawnablePetsSoundsProvider::new);
	}
}

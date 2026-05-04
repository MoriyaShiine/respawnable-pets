/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.datagen;

import moriyashiine.respawnablepets.datagen.provider.ModAdvancementProvider;
import moriyashiine.respawnablepets.datagen.provider.ModModelProvider;
import moriyashiine.respawnablepets.datagen.provider.ModRecipeProvider;
import moriyashiine.respawnablepets.datagen.provider.ModSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModAdvancementProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModSoundsProvider::new);
	}
}

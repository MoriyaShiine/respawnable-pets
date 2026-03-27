/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.level.StoredPetsComponent;
import net.minecraft.world.level.Level;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v8.level.LevelComponentFactoryRegistry;
import org.ladysnake.cca.api.v8.level.LevelComponentInitializer;

public class ModLevelComponents implements LevelComponentInitializer {
	public static final ComponentKey<StoredPetsComponent> STORED_PETS = ComponentRegistry.getOrCreate(RespawnablePets.id("stored_pets"), StoredPetsComponent.class);

	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.registerFor(Level.OVERWORLD, STORED_PETS, _ -> new StoredPetsComponent());
	}
}

/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.world.StoredPetsComponent;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class ModWorldComponents implements WorldComponentInitializer {
	public static final ComponentKey<StoredPetsComponent> STORED_PETS = ComponentRegistry.getOrCreate(RespawnablePets.id("stored_pets"), StoredPetsComponent.class);

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.registerFor(World.OVERWORLD, STORED_PETS, world -> new StoredPetsComponent());
	}
}

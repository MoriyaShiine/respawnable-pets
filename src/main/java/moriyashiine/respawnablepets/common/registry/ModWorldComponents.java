/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.registry;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.world.StoredPetsComponent;
import net.minecraft.util.Identifier;

public class ModWorldComponents implements WorldComponentInitializer {
	public static final ComponentKey<StoredPetsComponent> STORED_PETS = ComponentRegistry.getOrCreate(new Identifier(RespawnablePets.MOD_ID, "stored_pets"), StoredPetsComponent.class);

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(STORED_PETS, world -> new StoredPetsComponent());
	}
}

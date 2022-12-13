/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.registry;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import net.minecraft.entity.mob.MobEntity;

public class ModEntityComponents implements EntityComponentInitializer {
	public static final ComponentKey<RespawnableComponent> RESPAWNABLE = ComponentRegistry.getOrCreate(RespawnablePets.id("respawnable"), RespawnableComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(MobEntity.class, RESPAWNABLE).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(RespawnableComponent::new);
	}
}

/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.init;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import net.minecraft.entity.mob.MobEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class ModEntityComponents implements EntityComponentInitializer {
	public static final ComponentKey<RespawnableComponent> RESPAWNABLE = ComponentRegistry.getOrCreate(RespawnablePets.id("respawnable"), RespawnableComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(MobEntity.class, RESPAWNABLE).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(RespawnableComponent::new);
	}
}

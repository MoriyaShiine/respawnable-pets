/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.respawnablepets.common.component.world.StoredPetsComponent;
import moriyashiine.respawnablepets.common.registry.ModItems;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.registry.ModWorldComponents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class RespawnablePets implements ModInitializer {
	public static final String MOD_ID = "respawnablepets";

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, ModConfig.class);
		ModItems.init();
		ModSoundEvents.init();
		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (ModConfig.timeToRespawn < 0) {
				respawnPets(entity);
			}
		});
	}

	public static void respawnPets(LivingEntity living) {
		if (!living.world.isClient) {
			StoredPetsComponent storedPetsComponent = living.getServer().getOverworld().getComponent(ModWorldComponents.STORED_PETS);
			for (int i = storedPetsComponent.getStoredPets().size() - 1; i >= 0; i--) {
				NbtCompound nbt = storedPetsComponent.getStoredPets().get(i);
				if (living.getUuid().equals(nbt.getUuid("Owner"))) {
					LivingEntity pet = (LivingEntity) Registries.ENTITY_TYPE.get(new Identifier(nbt.getString("id"))).create(living.world);
					if (pet != null) {
						pet.readNbt(nbt);
						FabricDimensions.teleport(pet, (ServerWorld) living.world, new TeleportTarget(living.getPos(), Vec3d.ZERO, pet.getHeadYaw(), pet.getPitch()));
						pet.setHealth(pet.getMaxHealth());
						pet.extinguish();
						pet.setFrozenTicks(0);
						pet.clearStatusEffects();
						pet.fallDistance = 0;
						living.world.spawnEntity(pet);
						storedPetsComponent.getStoredPets().remove(i);
					}
				}
			}
		}
	}
}

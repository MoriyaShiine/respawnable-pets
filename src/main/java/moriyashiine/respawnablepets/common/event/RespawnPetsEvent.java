/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.ModConfig;
import moriyashiine.respawnablepets.common.component.world.StoredPetsComponent;
import moriyashiine.respawnablepets.common.init.ModWorldComponents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class RespawnPetsEvent {
	public static class Sleep implements EntitySleepEvents.StopSleeping {
		@Override
		public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
			if (ModConfig.respawnAfterSleep && !entity.getEntityWorld().isClient()) {
				long time = entity.getEntityWorld().getTimeOfDay() % 24000;
				if (time == 0 || time == 23461) {
					respawnPets(entity);
				}
			}
		}
	}

	public static class Tick implements ServerTickEvents.EndTick {
		@Override
		public void onEndTick(MinecraftServer server) {
			if (ModConfig.timeOfDayToRespawn >= 0) {
				for (PlayerEntity player : PlayerLookup.all(server)) {
					if (player.getEntityWorld().getTimeOfDay() % 24000 == ModConfig.timeOfDayToRespawn) {
						respawnPets(player);
					}
				}
			}
		}
	}

	private static void respawnPets(LivingEntity living) {
		StoredPetsComponent storedPetsComponent = ModWorldComponents.STORED_PETS.get(living.getEntityWorld().getServer().getOverworld());
		for (int i = storedPetsComponent.getStoredPets().size() - 1; i >= 0; i--) {
			int index = i;
			ReadView readView = NbtReadView.create(ErrorReporter.EMPTY, living.getRegistryManager(), storedPetsComponent.getStoredPets().get(index));
			LazyEntityReference<LivingEntity> lazy = LazyEntityReference.fromDataOrPlayerName(readView, "Owner", living.getEntityWorld());
			if (lazy != null && living.getUuid().equals(lazy.getUuid())) {
				readView.getOptionalString("id").ifPresent(id -> {
					LivingEntity pet = (LivingEntity) Registries.ENTITY_TYPE.get(Identifier.of(id)).create(living.getEntityWorld(), SpawnReason.TRIGGERED);
					if (pet != null) {
						pet.readData(readView);
						pet.teleportTo(new TeleportTarget((ServerWorld) living.getEntityWorld(), living.getEntityPos(), Vec3d.ZERO, pet.getHeadYaw(), pet.getPitch(), TeleportTarget.NO_OP));
						living.getEntityWorld().spawnEntity(pet);
						storedPetsComponent.getStoredPets().remove(index);
					}
				});
			}
		}
	}
}

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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class RespawnPetsEvent {
	public static class Sleep implements EntitySleepEvents.StopSleeping {
		@Override
		public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
			if (ModConfig.respawnAfterSleep && !entity.getWorld().isClient) {
				long time = entity.getWorld().getTimeOfDay() % 24000;
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
					if (player.getWorld().getTimeOfDay() % 24000 == ModConfig.timeOfDayToRespawn) {
						respawnPets(player);
					}
				}
			}
		}
	}

	private static void respawnPets(LivingEntity living) {
		StoredPetsComponent storedPetsComponent = ModWorldComponents.STORED_PETS.get(living.getServer().getOverworld());
		for (int i = storedPetsComponent.getStoredPets().size() - 1; i >= 0; i--) {
			NbtCompound nbt = storedPetsComponent.getStoredPets().get(i);
			if (living.getUuid().equals(nbt.getUuid("Owner"))) {
				LivingEntity pet = (LivingEntity) Registries.ENTITY_TYPE.get(Identifier.of(nbt.getString("id"))).create(living.getWorld(), SpawnReason.TRIGGERED);
				if (pet != null) {
					pet.readNbt(nbt);
					pet.teleportTo(new TeleportTarget((ServerWorld) living.getWorld(), living.getPos(), Vec3d.ZERO, pet.getHeadYaw(), pet.getPitch(), TeleportTarget.NO_OP));
					living.getWorld().spawnEntity(pet);
					storedPetsComponent.getStoredPets().remove(i);
				}
			}
		}
	}
}

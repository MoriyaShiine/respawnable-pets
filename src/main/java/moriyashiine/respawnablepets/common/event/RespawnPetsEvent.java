/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.ModConfig;
import moriyashiine.respawnablepets.common.component.level.StoredPetsComponent;
import moriyashiine.respawnablepets.common.init.ModLevelComponents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;

public class RespawnPetsEvent {
	public static class Sleep implements EntitySleepEvents.StopSleeping {
		@Override
		public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
			if (ModConfig.respawnAfterSleep && !entity.level().isClientSide()) {
				long time = entity.level().getDefaultClockTime() % 24000;
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
				for (Player player : PlayerLookup.all(server)) {
					if (player.level().getDefaultClockTime() % 24000 == ModConfig.timeOfDayToRespawn) {
						respawnPets(player);
					}
				}
			}
		}
	}

	private static void respawnPets(LivingEntity entity) {
		StoredPetsComponent storedPetsComponent = ModLevelComponents.STORED_PETS.get(entity.level().getServer().overworld());
		for (int i = storedPetsComponent.getStoredPets().size() - 1; i >= 0; i--) {
			int index = i;
			ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, entity.registryAccess(), storedPetsComponent.getStoredPets().get(index));
			EntityReference<LivingEntity> owner = EntityReference.readWithOldOwnerConversion(input, "Owner", entity.level());
			if (owner != null && entity.getUUID().equals(owner.getUUID())) {
				input.getString("id").ifPresent(id -> {
					LivingEntity pet = (LivingEntity) BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(id)).create(entity.level(), EntitySpawnReason.TRIGGERED);
					if (pet != null) {
						pet.load(input);
						pet.teleport(new TeleportTransition((ServerLevel) entity.level(), entity.position(), Vec3.ZERO, pet.getYHeadRot(), pet.getXRot(), TeleportTransition.DO_NOTHING));
						entity.level().addFreshEntity(pet);
						storedPetsComponent.getStoredPets().remove(index);
					}
				});
			}
		}
	}
}

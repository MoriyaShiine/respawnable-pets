/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.ModConfig;
import moriyashiine.respawnablepets.common.RespawnablePets;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class RespawnPetsEvent {
	public static class Sleep implements EntitySleepEvents.StopSleeping {
		@Override
		public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
			if (ModConfig.respawnAfterSleep && !entity.getWorld().isClient) {
				long time = entity.getWorld().getTimeOfDay() % 24000;
				if (time == 0 || time == 23461) {
					RespawnablePets.respawnPets(entity);
				}
			}
		}
	}

	public static class Tick implements ServerTickEvents.EndTick {
		@Override
		public void onEndTick(MinecraftServer server) {
			if (ModConfig.timeOfDayToRespawn >= 0) {
				for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
					if (player.getWorld().getTimeOfDay() % 24000 == ModConfig.timeOfDayToRespawn) {
						RespawnablePets.respawnPets(player);
					}
				}
			}
		}
	}
}

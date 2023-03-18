/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.ModConfig;
import moriyashiine.respawnablepets.common.RespawnablePets;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class RespawnPetsEvent implements EntitySleepEvents.StopSleeping {
	@Override
	public void onStopSleeping(LivingEntity entity, BlockPos sleepingPos) {
		if (ModConfig.timeToRespawn < 0) {
			RespawnablePets.respawnPets(entity);
		}
	}
}

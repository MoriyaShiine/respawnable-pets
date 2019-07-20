package moriyashiine.respawnablepets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

@SuppressWarnings("unused")
class ModEventHandler {
	@SubscribeEvent
	public void wakeUp(PlayerWakeUpEvent event) {
		PlayerEntity player = event.getEntityPlayer();
		World world = player.world;
		if (!world.isRemote) ExtendedWorld.get(world).trySpawn(world, player);
	}
	
	@SubscribeEvent
	public void damageLiving(LivingDamageEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote) {
			if (ExtendedWorld.get(world).containsEntity(entity) && entity.getHealth() - event.getAmount() <= 0 && !entity.serializeNBT().getString("OwnerUUID").isEmpty()) {
				event.setCanceled(true);
				entity.remove();
				if (world.getGameRules().func_223586_b(GameRules.field_223609_l)) {
					PlayerEntity player = world.getPlayerByUuid(UUID.fromString(entity.serializeNBT().getString("OwnerUUID")));
					if (player != null) player.sendMessage(entity.getCombatTracker().getDeathMessage());
				}
			}
		}
	}
}
package moriyashiine.respawnablepets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@SuppressWarnings("unused")
public class ModEventHandler {
	@SubscribeEvent
	public void onWakeUp(PlayerWakeUpEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (!world.isRemote) ExtendedWorld.get(world).trySpawn(world, player);
	}
	
	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		World world = living.world;
		if (!world.isRemote) {
			if (ExtendedWorld.get(world).containsEntity(living) && living.getHealth() - event.getAmount() <= 0 && !living.serializeNBT().getString("OwnerUUID").isEmpty()) {
				event.setCanceled(true);
				living.setDead();
				if (world.getGameRules().getBoolean("showDeathMessages")) {
					EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(living.serializeNBT().getString("OwnerUUID")));
					if (player != null) player.sendMessage(living.getCombatTracker().getDeathMessage());
				}
			}
		}
	}
}
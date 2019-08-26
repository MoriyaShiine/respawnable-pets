package moriyashiine.respawnablepets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.UUID;

public class ModEventHandler {
	@SubscribeEvent
	public void wakeUp(PlayerWakeUpEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = player.world;
		if (!world.isRemote) {
			ExtendedWorld ext = ExtendedWorld.get(world);
			for (int i = ext.pets.size() - 1; i >= 0; i--) {
				CompoundNBT tag = ext.pets.get(i);
				if (tag.getString("OwnerUUID").equalsIgnoreCase(player.getUniqueID().toString())) {
					LivingEntity entity = (LivingEntity) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("id")))).create(world);
					if (entity != null) {
						entity.deserializeNBT(tag);
						entity.setPositionAndRotation(player.posX, player.posY, player.posZ, world.rand.nextInt(360), 0);
						if (world.addEntity(entity)) {
							entity.heal(Float.MAX_VALUE);
							entity.extinguish();
							entity.clearActivePotions();
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void livingDamage(LivingDamageEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote && !(entity instanceof PlayerEntity)) {
			UUID owner = Util.getOwner(entity);
			ExtendedWorld ext = ExtendedWorld.get(world);
			if (owner != null && entity.getHealth() - event.getAmount() <= 0 && ext.containsEntity(entity)) {
				event.setCanceled(true);
				Util.removePet(entity);
				Util.addPet(entity);
				entity.remove();
				if (world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
					PlayerEntity player = Util.findPlayer(world, owner);
					if (player != null) player.sendMessage(entity.getCombatTracker().getDeathMessage());
				}
			}
		}
	}
}
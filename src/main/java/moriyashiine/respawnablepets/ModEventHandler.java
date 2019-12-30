package moriyashiine.respawnablepets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.UUID;

public class ModEventHandler {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void toggleRespawn(LivingAttackEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote && !(entity instanceof PlayerEntity)) {
			Entity attacker = event.getSource().getImmediateSource();
			if (attacker instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) attacker;
				if (player.getHeldItemMainhand().getItem() == RespawnablePets.Registry.etheric_gem) {
					event.setCanceled(true);
					UUID owner = Util.getOwner(entity);
					if (owner != null && owner.equals(player.getUniqueID())) {
						EntityType<?> type = entity.getType();
						if (Config.INSTANCE.blacklist.get().contains(Objects.requireNonNull(type.getRegistryName()).toString())) player.sendStatusMessage(new TranslationTextComponent(RespawnablePets.MODID + ".blacklisted", entity.getDisplayName()), true);
						else {
							if (Util.addPet(entity)) player.sendStatusMessage(new TranslationTextComponent(RespawnablePets.MODID + ".enable_respawn", entity.getDisplayName()), true);
							else if (Util.removePet(entity)) player.sendStatusMessage(new TranslationTextComponent(RespawnablePets.MODID + ".disable_respawn", entity.getDisplayName()), true);
						}
					}
					else player.sendStatusMessage(new TranslationTextComponent(RespawnablePets.MODID + ".not_owner", entity.getDisplayName()), true);
				}
			}
		}
	}
	
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
						BlockPos playerPos = player.getPosition();
						entity.setPositionAndRotation(playerPos.getX(), playerPos.getY(), playerPos.getZ(), world.rand.nextInt(360), 0);
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
			ExtendedWorld ext = ExtendedWorld.get(world);
			UUID owner = Util.getOwner(entity);
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
package moriyashiine.respawnablepets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Objects;
import java.util.UUID;

public class ModEventHandler {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void toggleRespawn(LivingAttackEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote && !(entity instanceof EntityPlayer)) {
			Entity attacker = event.getSource().getImmediateSource();
			if (attacker instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) attacker;
				if (player.getHeldItemMainhand().getItem() == RespawnablePets.Registry.etheric_gem) {
					event.setCanceled(true);
					UUID owner = Util.getOwner(entity);
					if (owner != null && owner.equals(player.getUniqueID())) {
						EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
						if (entry != null) {
							if (RespawnablePets.config.blacklist.contains(Objects.requireNonNull(entry.getRegistryName()).toString()))
								player.sendStatusMessage(new TextComponentTranslation(RespawnablePets.MODID + ".blacklisted", entity.getDisplayName()), true);
							else {
								if (Util.addPet(entity)) player.sendStatusMessage(new TextComponentTranslation(RespawnablePets.MODID + ".enable_respawn", entity.getDisplayName()), true);
								else if (Util.removePet(entity)) player.sendStatusMessage(new TextComponentTranslation(RespawnablePets.MODID + ".disable_respawn", entity.getDisplayName()), true);
							}
						}
					}
					else player.sendStatusMessage(new TextComponentTranslation(RespawnablePets.MODID + ".not_owner", entity.getDisplayName()), true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void wakeUp(PlayerWakeUpEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (!world.isRemote) {
			ExtendedWorld ext = ExtendedWorld.get();
			for (int i = ext.pets.size() - 1; i >= 0; i--) {
				NBTTagCompound tag = ext.pets.get(i);
				if (tag.getString("OwnerUUID").equalsIgnoreCase(player.getUniqueID().toString())) {
					EntityLivingBase entity = (EntityLivingBase) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("id")))).newInstance(world);
					if (entity != null) {
						entity.deserializeNBT(tag);
						BlockPos playerPos = player.getPosition();
						entity.setPositionAndRotation(playerPos.getX(), playerPos.getY(), playerPos.getZ(), world.rand.nextInt(360), 0);
						if (world.spawnEntity(entity)) {
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
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote && !(entity instanceof EntityPlayer)) {
			ExtendedWorld ext = ExtendedWorld.get();
			UUID owner = Util.getOwner(entity);
			if (owner != null && entity.getHealth() - event.getAmount() <= 0 && ext.containsEntity(entity)) {
				event.setCanceled(true);
				Util.removePet(entity);
				Util.addPet(entity);
				entity.setDead();
				if (!entity.isDead) entity.isDead = true;
				if (world.getGameRules().getBoolean("showDeathMessages")) {
					EntityPlayer player = Util.findPlayer(owner);
					if (player != null) player.sendMessage(entity.getCombatTracker().getDeathMessage());
				}
			}
		}
	}
}
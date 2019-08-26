package moriyashiine.respawnablepets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.UUID;

@SuppressWarnings("unused")
public class ModEventHandler {
	@SubscribeEvent
	public void wakeUp(PlayerWakeUpEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (!world.isRemote) {
			ExtendedWorld ext = ExtendedWorld.get();
			for (int i = ext.pets.size() - 1; i >= 0; i--) {
				NBTTagCompound tag = ext.pets.get(i);
				if (tag.getString("OwnerUUID").equalsIgnoreCase(player.getPersistentID().toString())) {
					EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("id")));
					if (entry != null) {
						EntityLivingBase entity = (EntityLivingBase) entry.newInstance(world);
						entity.deserializeNBT(tag);
						entity.setPositionAndRotation(player.posX, player.posY, player.posZ, world.rand.nextInt(360), 0);
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
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void livingDamage(LivingDamageEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote && !(entity instanceof EntityPlayer)) {
			UUID owner = Util.getOwner(entity);
			ExtendedWorld ext = ExtendedWorld.get();
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
	
	//sad
	//	@SubscribeEvent
	//	public void unload(ChunkEvent.Unload event)
	//	{
	//		Chunk chunk = event.getChunk();
	//		for (ClassInheritanceMultiMap<Entity> map : chunk.getEntityLists())
	//		{
	//			for (Entity entity : map)
	//			{
	//				if (entity instanceof EntityLivingBase)
	//				{
	//					UUID owner = Util.getOwner((EntityLivingBase) entity);
	//					if (owner != null)
	//					{
	//						EntityPlayer player = Util.findPlayer(owner);
	//						if (player != null && player.dimension == entity.dimension) entity.setPositionAndRotation(player.posX, player.posY, player.posZ, ((EntityLivingBase) entity).getRNG().nextInt(360), 0);
	//					}
	//				}
	//			}
	//		}
	//	}
}
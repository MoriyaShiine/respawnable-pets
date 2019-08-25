package moriyashiine.respawnablepets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.UUID;

class Util {
	static UUID getOwner(EntityLivingBase entity) {
		NBTTagCompound tag = entity.serializeNBT();
		String id = tag.getString("OwnerUUID");
		return id.isEmpty() ? null : UUID.fromString(id);
	}
	
	static EntityPlayer findPlayer(UUID uuid) {
		for (WorldServer ws : DimensionManager.getWorlds()) {
			EntityPlayer player = ws.getPlayerEntityByUUID(uuid);
			if (player != null) return player;
		}
		return null;
	}
	
	static boolean addPet(EntityLivingBase entity) {
		ExtendedWorld ext = ExtendedWorld.get();
		if (!ext.containsEntity(entity)) {
			ext.pets.add(entity.serializeNBT());
			ext.markDirty();
			return true;
		}
		return false;
	}
	
	static boolean removePet(EntityLivingBase entity) {
		boolean flag = false;
		ExtendedWorld ext = ExtendedWorld.get();
		for (int i = ext.pets.size() - 1; i >= 0; i--) {
			UUID id = ext.pets.get(i).getUniqueId("UUID");
			if (id != null && id.equals(entity.getPersistentID())) {
				ext.pets.remove(i);
				ext.markDirty();
				flag = true;
			}
		}
		return flag;
	}
}
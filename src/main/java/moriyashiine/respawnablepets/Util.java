package moriyashiine.respawnablepets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Objects;
import java.util.UUID;

class Util {
	static UUID getOwner(LivingEntity entity) {
		CompoundNBT tag = entity.serializeNBT();
		String id = tag.getString("OwnerUUID");
		return id.isEmpty() ? null : UUID.fromString(id);
	}
	
	static PlayerEntity findPlayer(World world, UUID uuid) {
		for (ServerWorld sw : Objects.requireNonNull(world.getServer()).getWorlds()) {
			PlayerEntity player = sw.getPlayerByUuid(uuid);
			if (player != null) return player;
		}
		return null;
	}
	
	static boolean addPet(LivingEntity entity) {
		ExtendedWorld ext = ExtendedWorld.get(entity.world);
		if (!ext.containsEntity(entity)) {
			ext.pets.add(entity.serializeNBT());
			ext.markDirty();
			return true;
		}
		return false;
	}
	
	static boolean removePet(LivingEntity entity) {
		boolean flag = false;
		ExtendedWorld ext = ExtendedWorld.get(entity.world);
		for (int i = ext.pets.size() - 1; i >= 0; i--) {
			if (ext.pets.get(i).getUniqueId("UUID").equals(entity.getUniqueID())) {
				ext.pets.remove(i);
				ext.markDirty();
				flag = true;
			}
		}
		return flag;
	}
}
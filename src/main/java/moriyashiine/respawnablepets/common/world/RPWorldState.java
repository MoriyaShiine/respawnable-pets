package moriyashiine.respawnablepets.common.world;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RPWorldState extends PersistentState {
	public final List<NbtCompound> storedPets = new ArrayList<>();
	public final List<UUID> petsToRespawn = new ArrayList<>();
	
	public static RPWorldState readNbt(NbtCompound nbt) {
		RPWorldState worldState = new RPWorldState();
		NbtList storedPets = nbt.getList("StoredPets", NbtType.COMPOUND);
		for (int i = 0; i < storedPets.size(); i++) {
			worldState.storedPets.add(storedPets.getCompound(i));
		}
		NbtList petsToRespawn = nbt.getList("PetsToRespawn", NbtType.COMPOUND);
		for (int i = 0; i < petsToRespawn.size(); i++) {
			worldState.petsToRespawn.add(petsToRespawn.getCompound(i).getUuid("UUID"));
		}
		return worldState;
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtList storedPets = new NbtList();
		storedPets.addAll(this.storedPets);
		nbt.put("StoredPets", storedPets);
		NbtList petsToRespawn = new NbtList();
		for (UUID uuid : this.petsToRespawn) {
			NbtCompound pet = new NbtCompound();
			pet.putUuid("UUID", uuid);
			petsToRespawn.add(pet);
		}
		nbt.put("PetsToRespawn", petsToRespawn);
		return nbt;
	}
	
	@SuppressWarnings("ConstantConditions")
	public static RPWorldState get(World world) {
		return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(RPWorldState::readNbt, RPWorldState::new, RespawnablePets.MODID);
	}
}

/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.world;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ModWorldState extends PersistentState {
	public final List<NbtCompound> storedPets = new ArrayList<>();

	public static ModWorldState readNbt(NbtCompound nbt) {
		ModWorldState worldState = new ModWorldState();
		NbtList storedPets = nbt.getList("StoredPets", NbtType.COMPOUND);
		for (int i = 0; i < storedPets.size(); i++) {
			worldState.storedPets.add(storedPets.getCompound(i));
		}
		return worldState;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtList storedPets = new NbtList();
		storedPets.addAll(this.storedPets);
		nbt.put("StoredPets", storedPets);
		return nbt;
	}

	@SuppressWarnings("ConstantConditions")
	public static ModWorldState get(World world) {
		return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(ModWorldState::readNbt, ModWorldState::new, RespawnablePets.MOD_ID);
	}
}

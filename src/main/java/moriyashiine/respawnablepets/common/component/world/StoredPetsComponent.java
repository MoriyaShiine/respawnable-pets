/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.component.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.ArrayList;
import java.util.List;

public class StoredPetsComponent implements Component {
	private final List<NbtCompound> storedPets = new ArrayList<>();

	@Override
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		NbtList storedPets = tag.getList("StoredPets", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < storedPets.size(); i++) {
			this.storedPets.add(storedPets.getCompound(i));
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		NbtList storedPets = new NbtList();
		storedPets.addAll(this.storedPets);
		tag.put("StoredPets", storedPets);
	}

	public List<NbtCompound> getStoredPets() {
		return storedPets;
	}
}

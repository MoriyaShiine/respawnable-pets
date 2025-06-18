/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.component.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.ArrayList;
import java.util.List;

public class StoredPetsComponent implements Component {
	private final List<NbtCompound> storedPets = new ArrayList<>();

	@Override
	public void readData(ReadView readView) {
		readView.read("StoredPets", NbtCompound.CODEC.listOf()).ifPresent(storedPets::addAll);
	}

	@Override
	public void writeData(WriteView writeView) {
		writeView.put("StoredPets", NbtCompound.CODEC.listOf(), storedPets);
	}

	public List<NbtCompound> getStoredPets() {
		return storedPets;
	}
}

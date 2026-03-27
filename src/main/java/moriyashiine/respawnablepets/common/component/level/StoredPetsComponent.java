/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.component.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

import java.util.ArrayList;
import java.util.List;

public class StoredPetsComponent implements CardinalComponent {
	private final List<CompoundTag> storedPets = new ArrayList<>();

	@Override
	public void readData(ValueInput input) {
		input.read("StoredPets", CompoundTag.CODEC.listOf()).ifPresent(storedPets::addAll);
	}

	@Override
	public void writeData(ValueOutput output) {
		output.store("StoredPets", CompoundTag.CODEC.listOf(), storedPets);
	}

	public List<CompoundTag> getStoredPets() {
		return storedPets;
	}
}

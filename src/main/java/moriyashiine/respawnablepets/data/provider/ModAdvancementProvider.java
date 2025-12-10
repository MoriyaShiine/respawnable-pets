/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.data.provider;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.init.ModCriterion;
import moriyashiine.respawnablepets.common.init.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
	public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
		Advancement.Builder.create()
				.parent(Identifier.tryParse("husbandry/tame_an_animal"))
				.display(ModItems.ETHERIC_GEM,
						Text.translatable("advancements.respawnablepets.husbandry.make_pet_respawnable.title"),
						Text.translatable("advancements.respawnablepets.husbandry.make_pet_respawnable.description"),
						null,
						AdvancementFrame.TASK,
						true,
						true,
						false)
				.criterion("make_pet_respawnable", ModCriterion.MAKE_PET_RESPAWNABLE.create(new TickCriterion.Conditions(Optional.empty())))
				.build(consumer, RespawnablePets.id("husbandry/make_pet_respawnable").toString());
	}
}

/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.datagen.provider;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.init.ModItems;
import moriyashiine.respawnablepets.common.init.ModTriggers;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
	public ModAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void generateAdvancement(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
		Advancement.Builder.advancement()
				.parent(Identifier.tryParse("husbandry/tame_an_animal"))
				.display(ModItems.ETHERIC_GEM,
						Component.translatable("advancements.respawnablepets.husbandry.make_pet_respawnable.title"),
						Component.translatable("advancements.respawnablepets.husbandry.make_pet_respawnable.description"),
						null,
						AdvancementType.TASK,
						true,
						true,
						false)
				.addCriterion("make_pet_respawnable", ModTriggers.MAKE_PET_RESPAWNABLE.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
				.save(consumer, RespawnablePets.id("husbandry/make_pet_respawnable").toString());
	}
}

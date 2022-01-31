/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import moriyashiine.respawnablepets.common.registry.ModItems;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.world.ModWorldState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RespawnablePets implements ModInitializer {
	public static final String MOD_ID = "respawnablepets";

	public static ModConfig config;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		ModItems.init();
		ModSoundEvents.init();
		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (RespawnablePets.config.timeToRespawn < 0) {
				respawnPets(entity);
			}
		});
	}

	public static void respawnPets(LivingEntity living) {
		if (!living.world.isClient) {
			ModWorldState worldState = ModWorldState.get(living.world);
			for (int i = worldState.storedPets.size() - 1; i >= 0; i--) {
				NbtCompound nbt = worldState.storedPets.get(i);
				if (living.getUuid().equals(nbt.getUuid("Owner"))) {
					LivingEntity pet = (LivingEntity) Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("id"))).create(living.world);
					if (pet != null) {
						pet.readNbt(nbt);
						pet.moveToWorld((ServerWorld) living.world);
						pet.teleport(living.getX() + 0.5, living.getY() + 0.5, living.getZ() + 0.5);
						pet.setHealth(pet.getMaxHealth());
						pet.extinguish();
						pet.setFrozenTicks(0);
						pet.clearStatusEffects();
						pet.fallDistance = 0;
						living.world.spawnEntity(pet);
						worldState.storedPets.remove(i);
						worldState.markDirty();
					}
				}
			}
		}
	}
}

/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.respawnablepets.common.component.world.StoredPetsComponent;
import moriyashiine.respawnablepets.common.event.RespawnPetsEvent;
import moriyashiine.respawnablepets.common.event.StorePetEvent;
import moriyashiine.respawnablepets.common.init.ModCriterion;
import moriyashiine.respawnablepets.common.init.ModItems;
import moriyashiine.respawnablepets.common.init.ModSoundEvents;
import moriyashiine.respawnablepets.common.init.ModWorldComponents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class RespawnablePets implements ModInitializer {
	public static final String MOD_ID = "respawnablepets";

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, ModConfig.class);
		ModItems.init();
		ModSoundEvents.init();
		ModCriterion.init();
		ServerLivingEntityEvents.ALLOW_DEATH.register(new StorePetEvent());
		EntitySleepEvents.STOP_SLEEPING.register(new RespawnPetsEvent.Sleep());
		ServerTickEvents.END_SERVER_TICK.register(new RespawnPetsEvent.Tick());
	}

	public static Identifier id(String value) {
		return Identifier.of(MOD_ID, value);
	}

	public static void respawnPets(LivingEntity living) {
		StoredPetsComponent storedPetsComponent = ModWorldComponents.STORED_PETS.get(living.getServer().getOverworld());
		for (int i = storedPetsComponent.getStoredPets().size() - 1; i >= 0; i--) {
			NbtCompound nbt = storedPetsComponent.getStoredPets().get(i);
			if (living.getUuid().equals(nbt.getUuid("Owner"))) {
				LivingEntity pet = (LivingEntity) Registries.ENTITY_TYPE.get(Identifier.of(nbt.getString("id"))).create(living.getWorld(), SpawnReason.TRIGGERED);
				if (pet != null) {
					pet.readNbt(nbt);
					pet.teleportTo(new TeleportTarget((ServerWorld) living.getWorld(), living.getPos(), Vec3d.ZERO, pet.getHeadYaw(), pet.getPitch(), TeleportTarget.NO_OP));
					living.getWorld().spawnEntity(pet);
					storedPetsComponent.getStoredPets().remove(i);
				}
			}
		}
	}
}

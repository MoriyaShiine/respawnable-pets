/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.respawnablepets.common.component.world.StoredPetsComponent;
import moriyashiine.respawnablepets.common.event.RespawnPetsEvent;
import moriyashiine.respawnablepets.common.event.StorePetEvent;
import moriyashiine.respawnablepets.common.registry.ModCriterion;
import moriyashiine.respawnablepets.common.registry.ModItems;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.registry.ModWorldComponents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
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
		EntitySleepEvents.STOP_SLEEPING.register(new RespawnPetsEvent());
	}

	public static Identifier id(String value) {
		return new Identifier(MOD_ID, value);
	}

	public static void respawnPets(LivingEntity living) {
		if (!living.world.isClient) {
			StoredPetsComponent storedPetsComponent = ModWorldComponents.STORED_PETS.get(living.getServer().getOverworld());
			for (int i = storedPetsComponent.getStoredPets().size() - 1; i >= 0; i--) {
				NbtCompound nbt = storedPetsComponent.getStoredPets().get(i);
				if (living.getUuid().equals(nbt.getUuid("Owner"))) {
					LivingEntity pet = (LivingEntity) Registries.ENTITY_TYPE.get(new Identifier(nbt.getString("id"))).create(living.world);
					if (pet != null) {
						pet.readNbt(nbt);
						FabricDimensions.teleport(pet, (ServerWorld) living.world, new TeleportTarget(living.getPos(), Vec3d.ZERO, pet.getHeadYaw(), pet.getPitch()));
						living.world.spawnEntity(pet);
						storedPetsComponent.getStoredPets().remove(i);
					}
				}
			}
		}
	}
}

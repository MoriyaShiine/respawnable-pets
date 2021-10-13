package moriyashiine.respawnablepets.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import moriyashiine.respawnablepets.common.registry.ModItems;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.world.ModWorldState;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

public class RespawnablePets implements ModInitializer {
	public static final String MOD_ID = "respawnablepets";
	
	public static ModConfig config;
	
	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		ModItems.init();
		ModSoundEvents.init();
	}
	
	@SuppressWarnings("ConstantConditions")
	public static PlayerEntity findOwner(World world, UUID uuid) {
		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			PlayerEntity player = serverWorld.getPlayerByUuid(uuid);
			if (player != null) {
				return player;
			}
		}
		return null;
	}
	
	public static boolean isPetRespawnable(ModWorldState worldState, Entity entity) {
		for (UUID uuid : worldState.petsToRespawn) {
			if (entity.getUuid().equals(uuid)) {
				return true;
			}
		}
		return false;
	}
	
	public static void respawnPets(World world, Entity entity) {
		if (!world.isClient) {
			ModWorldState worldState = ModWorldState.get(world);
			for (int i = worldState.storedPets.size() - 1; i >= 0; i--) {
				NbtCompound nbt = worldState.storedPets.get(i);
				if (entity.getUuid().equals(nbt.getUuid("Owner"))) {
					LivingEntity pet = (LivingEntity) Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("id"))).create(world);
					if (pet != null) {
						pet.readNbt(nbt);
						pet.moveToWorld((ServerWorld) world);
						pet.teleport(entity.getX() + 0.5, entity.getY() + 0.5, entity.getZ() + 0.5);
						pet.setHealth(pet.getMaxHealth());
						pet.extinguish();
						pet.setFrozenTicks(0);
						pet.clearStatusEffects();
						pet.fallDistance = 0;
						world.spawnEntity(pet);
						worldState.storedPets.remove(i);
						worldState.markDirty();
					}
				}
			}
		}
	}
}

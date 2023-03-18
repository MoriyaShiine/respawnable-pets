/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.client.packet.SpawnSmokeParticlesPacket;
import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.registry.ModEntityComponents;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.registry.ModWorldComponents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StorePetEvent implements ServerLivingEntityEvents.AllowDeath {
	@Override
	public boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
		RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.getNullable(entity);
		if (respawnableComponent != null && respawnableComponent.getRespawnable()) {
			healPet(entity);
			NbtCompound stored = new NbtCompound();
			entity.saveSelfNbt(stored);
			ModWorldComponents.STORED_PETS.get(entity.getServer().getOverworld()).getStoredPets().add(stored);
			PlayerLookup.tracking(entity).forEach(foundPlayer -> SpawnSmokeParticlesPacket.send(foundPlayer, entity));
			entity.playSound(ModSoundEvents.ENTITY_GENERIC_TELEPORT, 1, 1);
			entity.remove(Entity.RemovalReason.DISCARDED);
			PlayerEntity owner = findOwnerByUUID(entity.world, stored.getUuid("Owner"));
			if (owner != null && entity.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
				owner.sendMessage(entity.getDamageTracker().getDeathMessage(), false);
			}
			return false;
		}
		return true;
	}

	@Nullable
	private static PlayerEntity findOwnerByUUID(World world, UUID uuid) {
		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			PlayerEntity player = serverWorld.getPlayerByUuid(uuid);
			if (player != null) {
				return player;
			}
		}
		return null;
	}

	private static void healPet(LivingEntity entity) {
		entity.setHealth(entity.getMaxHealth());
		entity.extinguish();
		entity.setFrozenTicks(0);
		entity.clearStatusEffects();
		entity.fallDistance = 0;
	}
}

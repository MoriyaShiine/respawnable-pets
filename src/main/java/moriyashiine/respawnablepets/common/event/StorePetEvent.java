/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.init.ModEntityComponents;
import moriyashiine.respawnablepets.common.init.ModSoundEvents;
import moriyashiine.respawnablepets.common.init.ModWorldComponents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
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
			((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY(), entity.getZ(), 32, entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth() / 2, 0);
			entity.playSound(ModSoundEvents.ENTITY_GENERIC_TELEPORT, 1, 1);
			entity.remove(Entity.RemovalReason.DISCARDED);
			if (entity instanceof Tameable tameable && entity.getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
				PlayerEntity owner = findOwnerByUUID(entity.getWorld(), tameable.getOwnerUuid());
				if (owner != null) {
					owner.sendMessage(entity.getDamageTracker().getDeathMessage(), false);
				}
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

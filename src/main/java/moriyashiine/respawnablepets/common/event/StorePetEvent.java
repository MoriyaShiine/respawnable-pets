/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.init.ModEntityComponents;
import moriyashiine.respawnablepets.common.init.ModSoundEvents;
import moriyashiine.respawnablepets.common.init.ModWorldComponents;
import moriyashiine.strawberrylib.api.module.SLibUtils;
import moriyashiine.strawberrylib.api.objects.enums.ParticleAnchor;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class StorePetEvent implements ServerLivingEntityEvents.AllowDeath {
	@Override
	public boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
		RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.getNullable(entity);
		if (respawnableComponent != null && respawnableComponent.isRespawnable()) {
			ServerWorld world = (ServerWorld) entity.getWorld();
			healPet(entity);
			NbtCompound stored = new NbtCompound();
			entity.saveSelfNbt(stored);
			ModWorldComponents.STORED_PETS.get(entity.getServer().getOverworld()).getStoredPets().add(stored);
			SLibUtils.addParticles(entity, ParticleTypes.SMOKE, 32, ParticleAnchor.BODY);
			SLibUtils.playSound(entity, ModSoundEvents.ENTITY_GENERIC_TELEPORT);
			entity.remove(Entity.RemovalReason.DISCARDED);
			if (entity instanceof Tameable tameable && world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && tameable.getOwner() instanceof ServerPlayerEntity playerOwner) {
				playerOwner.sendMessage(entity.getDamageTracker().getDeathMessage());
			}
			return false;
		}
		return true;
	}

	private static void healPet(LivingEntity entity) {
		entity.setHealth(entity.getMaxHealth());
		entity.extinguish();
		entity.setFrozenTicks(0);
		entity.clearStatusEffects();
		entity.fallDistance = 0;
	}
}

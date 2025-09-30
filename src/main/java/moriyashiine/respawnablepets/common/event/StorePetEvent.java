/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.init.ModEntityComponents;
import moriyashiine.respawnablepets.common.init.ModSoundEvents;
import moriyashiine.respawnablepets.common.init.ModWorldComponents;
import moriyashiine.strawberrylib.api.module.SLibUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.world.GameRules;

public class StorePetEvent implements ServerLivingEntityEvents.AllowDeath {
	@Override
	public boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
		RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.getNullable(entity);
		if (respawnableComponent != null && respawnableComponent.isRespawnable()) {
			ServerWorld world = (ServerWorld) entity.getEntityWorld();
			refreshPet(entity);
			NbtWriteView stored = NbtWriteView.create(ErrorReporter.EMPTY, entity.getRegistryManager());
			entity.saveSelfData(stored);
			ModWorldComponents.STORED_PETS.get(world.getServer().getOverworld()).getStoredPets().add(stored.getNbt());
			SLibUtils.playSound(entity, ModSoundEvents.ENTITY_GENERIC_TELEPORT);
			world.spawnParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY() + entity.getHeight() / 2, entity.getZ(), 32, entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth() / 2, 0);
			entity.remove(Entity.RemovalReason.DISCARDED);
			if (entity instanceof Tameable tameable && world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && tameable.getOwner() instanceof ServerPlayerEntity playerOwner) {
				playerOwner.sendMessage(entity.getDamageTracker().getDeathMessage());
			}
			return false;
		}
		return true;
	}

	private static void refreshPet(LivingEntity entity) {
		entity.setHealth(entity.getMaxHealth());
		entity.extinguish();
		entity.setFrozenTicks(0);
		entity.clearStatusEffects();
		entity.fallDistance = 0;
		if (entity instanceof Angerable angerable) {
			angerable.stopAnger();
		}
		if (entity instanceof TameableEntity tameable) {
			tameable.setSitting(false);
		}
	}
}

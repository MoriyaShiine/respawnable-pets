/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.event;

import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.init.ModEntityComponents;
import moriyashiine.respawnablepets.common.init.ModLevelComponents;
import moriyashiine.respawnablepets.common.init.ModSoundEvents;
import moriyashiine.strawberrylib.api.module.SLibUtils;
import moriyashiine.strawberrylib.api.objects.enums.ParticleAnchor;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.TagValueOutput;

public class StorePetEvent implements ServerLivingEntityEvents.AllowDeath {
	@Override
	public boolean allowDeath(LivingEntity entity, DamageSource source, float damageAmount) {
		RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.getNullable(entity);
		if (respawnableComponent != null && respawnableComponent.isRespawnable()) {
			ServerLevel level = (ServerLevel) entity.level();
			refreshPet(entity);
			TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
			entity.saveAsPassenger(output);
			ModLevelComponents.STORED_PETS.get(level.getServer().overworld()).getStoredPets().add(output.buildResult());
			SLibUtils.addParticles(entity, ParticleTypes.SMOKE, 32, ParticleAnchor.BODY);
			SLibUtils.playSound(entity, ModSoundEvents.ENTITY_GENERIC_TELEPORT);
			entity.remove(Entity.RemovalReason.DISCARDED);
			if (entity instanceof OwnableEntity ownable && level.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES) && ownable.getOwner() instanceof ServerPlayer player) {
				player.sendSystemMessage(entity.getCombatTracker().getDeathMessage());
			}
			return false;
		}
		return true;
	}

	private static void refreshPet(LivingEntity entity) {
		entity.setHealth(entity.getMaxHealth());
		entity.clearFire();
		entity.setTicksFrozen(0);
		entity.removeAllEffects();
		entity.fallDistance = 0;
		if (entity instanceof NeutralMob neutralMob) {
			neutralMob.stopBeingAngry();
		}
		if (entity instanceof TamableAnimal tameable) {
			tameable.setOrderedToSit(false);
		}
	}
}

/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.mixin;

import moriyashiine.respawnablepets.client.packet.SpawnSmokeParticlesPacket;
import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.registry.ModEntityComponents;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.registry.ModWorldComponents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract DamageTracker getDamageTracker();

	@Shadow
	public abstract float getHealth();

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "applyDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getHealth()F"), cancellable = true)
	private void respawnablepets$storePetOnDeath(DamageSource source, float amount, CallbackInfo ci) {
		if (!world.isClient && getHealth() - amount <= 0) {
			RespawnableComponent respawnableComponent = getComponent(ModEntityComponents.RESPAWNABLE);
			if (respawnableComponent.getRespawnable()) {
				NbtCompound stored = new NbtCompound();
				saveSelfNbt(stored);
				getServer().getOverworld().getComponent(ModWorldComponents.STORED_PETS).getStoredPets().add(stored);
				PlayerLookup.tracking(this).forEach(foundPlayer -> SpawnSmokeParticlesPacket.send(foundPlayer, this));
				playSound(ModSoundEvents.ENTITY_GENERIC_TELEPORT, 1, 1);
				remove(RemovalReason.DISCARDED);
				PlayerEntity owner = findOwnerByUUID(stored.getUuid("Owner"));
				if (owner != null && world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
					owner.sendMessage(getDamageTracker().getDeathMessage(), false);
				}
				ci.cancel();
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Unique
	private PlayerEntity findOwnerByUUID(UUID uuid) {
		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			PlayerEntity player = serverWorld.getPlayerByUuid(uuid);
			if (player != null) {
				return player;
			}
		}
		return null;
	}
}

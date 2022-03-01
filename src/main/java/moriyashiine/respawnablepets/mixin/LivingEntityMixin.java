/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.mixin;

import moriyashiine.respawnablepets.client.packet.SpawnSmokeParticlesPacket;
import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.registry.ModEntityTypeTags;
import moriyashiine.respawnablepets.common.registry.ModItems;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.world.ModWorldState;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract DamageTracker getDamageTracker();

	@Shadow
	public abstract float getHealth();

	@Shadow
	protected abstract float getSoundVolume();

	@Shadow
	public abstract float getSoundPitch();

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void respawnablepets$respawnPetsOnTick(CallbackInfo ci) {
		int timeToRespawn = RespawnablePets.config.timeToRespawn;
		if (timeToRespawn >= 0 && world.getTimeOfDay() % 24000 == timeToRespawn) {
			RespawnablePets.respawnPets(LivingEntity.class.cast(this));
		}
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void respawnablepets$togglePetRespawning(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClient) {
			Entity attacker = source.getSource();
			if (attacker instanceof PlayerEntity player) {
				if (player.getMainHandStack().isOf(ModItems.ETHERIC_GEM)) {
					NbtCompound stored = writeNbt(new NbtCompound());
					if (stored.containsUuid("Owner") && player.getUuid().equals(stored.getUuid("Owner"))) {
						if (getType().isIn(ModEntityTypeTags.BLACKLISTED)) {
							player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.blacklisted", getDisplayName()), true);
						} else {
							ModWorldState worldState = ModWorldState.get(world);
							if (canRespawn(worldState)) {
								player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.disable_respawn", getDisplayName()), true);
								for (int i = worldState.petsToRespawn.size() - 1; i >= 0; i--) {
									if (worldState.petsToRespawn.get(i).equals(getUuid())) {
										worldState.petsToRespawn.remove(i);
										worldState.markDirty();
									}
								}
							} else {
								player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.enable_respawn", getDisplayName()), true);
								worldState.petsToRespawn.add(getUuid());
								worldState.markDirty();
							}

						}
					} else {
						player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.not_owner", getDisplayName()), true);
					}
					cir.cancel();
				}
			}
		}
	}

	@Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHealth()F"), cancellable = true)
	private void respawnablepets$storePetOnDeath(DamageSource source, float amount, CallbackInfo ci) {
		if (!world.isClient) {
			ModWorldState worldState = ModWorldState.get(world);
			if (getHealth() - amount <= 0 && canRespawn(worldState)) {
				NbtCompound stored = new NbtCompound();
				saveSelfNbt(stored);
				worldState.storedPets.add(stored);
				worldState.markDirty();
				PlayerLookup.tracking(this).forEach(foundPlayer -> SpawnSmokeParticlesPacket.send(foundPlayer, this));
				playSound(ModSoundEvents.ENTITY_GENERIC_TELEPORT, getSoundVolume(), getSoundPitch());
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

	@Unique
	private boolean canRespawn(ModWorldState worldState) {
		for (UUID uuid : worldState.petsToRespawn) {
			if (getUuid().equals(uuid)) {
				return true;
			}
		}
		return false;
	}
}

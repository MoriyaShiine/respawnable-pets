package moriyashiine.respawnablepets.mixin;

import moriyashiine.respawnablepets.client.network.message.SpawnSmokeParticlesPacket;
import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.registry.ModItems;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import moriyashiine.respawnablepets.common.registry.ModTags;
import moriyashiine.respawnablepets.common.world.ModWorldState;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	private void tick(CallbackInfo callbackInfo) {
		int timeToRespawn = RespawnablePets.config.timeToRespawn;
		if (timeToRespawn >= 0 && world.getTimeOfDay() % 24000 == timeToRespawn) {
			RespawnablePets.respawnPets(world, this);
		}
	}
	
	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!world.isClient) {
			Entity attacker = source.getSource();
			if (attacker instanceof PlayerEntity player) {
				if (player.getMainHandStack().isOf(ModItems.ETHERIC_GEM)) {
					NbtCompound stored = writeNbt(new NbtCompound());
					if (stored.containsUuid("Owner") && player.getUuid().equals(stored.getUuid("Owner"))) {
						if (ModTags.BLACKLISTED.contains(getType())) {
							player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.blacklisted", getDisplayName()), true);
						}
						else {
							ModWorldState worldState = ModWorldState.get(world);
							if (RespawnablePets.isPetRespawnable(worldState, this)) {
								player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.disable_respawn", getDisplayName()), true);
								for (int i = worldState.petsToRespawn.size() - 1; i >= 0; i--) {
									if (worldState.petsToRespawn.get(i).equals(getUuid())) {
										worldState.petsToRespawn.remove(i);
										worldState.markDirty();
									}
								}
							}
							else {
								player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.enable_respawn", getDisplayName()), true);
								worldState.petsToRespawn.add(getUuid());
								worldState.markDirty();
							}
							
						}
					}
					else {
						player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.not_owner", getDisplayName()), true);
					}
					callbackInfo.cancel();
				}
			}
		}
	}
	
	@ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHealth()F"))
	private float modifyApplyDamage(float amount, DamageSource source) {
		if (!world.isClient) {
			ModWorldState worldState = ModWorldState.get(world);
			if (getHealth() - amount <= 0 && RespawnablePets.isPetRespawnable(worldState, this)) {
				NbtCompound stored = new NbtCompound();
				saveSelfNbt(stored);
				worldState.storedPets.add(stored);
				worldState.markDirty();
				PlayerLookup.tracking(this).forEach(foundPlayer -> SpawnSmokeParticlesPacket.send(foundPlayer, this));
				world.playSound(null, getBlockPos(), ModSoundEvents.ENTITY_GENERIC_TELEPORT, getSoundCategory(), getSoundVolume(), getSoundPitch());
				remove(RemovalReason.DISCARDED);
				PlayerEntity owner = RespawnablePets.findOwner(world, stored.getUuid("Owner"));
				if (owner != null && world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
					owner.sendMessage(getDamageTracker().getDeathMessage(), false);
				}
				return 0;
			}
		}
		return amount;
	}
	
	@Inject(method = "wakeUp", at = @At("HEAD"))
	private void wakeUp(CallbackInfo callbackInfo) {
		if (RespawnablePets.config.timeToRespawn < 0) {
			RespawnablePets.respawnPets(world, this);
		}
	}
}

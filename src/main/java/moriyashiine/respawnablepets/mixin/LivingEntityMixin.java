package moriyashiine.respawnablepets.mixin;

import moriyashiine.respawnablepets.client.network.message.SpawnSmokeParticlesPacket;
import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.world.RPWorldState;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	private static final Tag<EntityType<?>> BLACKLIST = TagRegistry.entityType(new Identifier(RespawnablePets.MODID, "blacklisted"));
	
	@Shadow
	public abstract DamageTracker getDamageTracker();
	
	@Shadow
	public abstract float getHealth();
	
	@Shadow
	protected abstract float getSoundVolume();
	
	@Shadow
	protected abstract float getSoundPitch();
	
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!world.isClient) {
			Entity attacker = source.getSource();
			if (attacker instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) attacker;
				if (player.getMainHandStack().getItem() == RespawnablePets.ETHERIC_GEM) {
					CompoundTag stored = toTag(new CompoundTag());
					if (stored.containsUuid("Owner") && player.getUuid().equals(stored.getUuid("Owner"))) {
						if (BLACKLIST.contains(getType())) {
							player.sendMessage(new TranslatableText(RespawnablePets.MODID + ".message.blacklisted", getDisplayName()), true);
						}
						else {
							RPWorldState worldState = RPWorldState.get(world);
							if (isPetRespawnable(worldState, this)) {
								player.sendMessage(new TranslatableText(RespawnablePets.MODID + ".message.disable_respawn", getDisplayName()), true);
								for (int i = worldState.petsToRespawn.size() - 1; i >= 0; i--) {
									if (worldState.petsToRespawn.get(i).equals(getUuid())) {
										worldState.petsToRespawn.remove(i);
										worldState.markDirty();
									}
								}
							}
							else {
								player.sendMessage(new TranslatableText(RespawnablePets.MODID + ".message.enable_respawn", getDisplayName()), true);
								worldState.petsToRespawn.add(getUuid());
								worldState.markDirty();
							}
							
						}
					}
					else {
						player.sendMessage(new TranslatableText(RespawnablePets.MODID + ".message.not_owner", getDisplayName()), true);
					}
					callbackInfo.cancel();
				}
			}
		}
	}
	
	@ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/LivingEntity;getHealth()F"))
	private float applyDamage(float amount, DamageSource source) {
		if (!world.isClient) {
			RPWorldState worldState = RPWorldState.get(world);
			if (getHealth() - amount <= 0 && isPetRespawnable(worldState, this)) {
				CompoundTag stored = new CompoundTag();
				saveSelfToTag(stored);
				worldState.storedPets.add(stored);
				worldState.markDirty();
				PlayerLookup.tracking(this).forEach(foundPlayer -> SpawnSmokeParticlesPacket.send(foundPlayer, this));
				world.playSound(null, getBlockPos(), RespawnablePets.ENTITY_GENERIC_TELEPORT, getSoundCategory(), getSoundVolume(), getSoundPitch());
				removed = true;
				PlayerEntity owner = findPlayer(world, stored.getUuid("Owner"));
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
		if (!world.isClient) {
			RPWorldState worldState = RPWorldState.get(world);
			for (int i = worldState.storedPets.size() - 1; i >= 0; i--) {
				CompoundTag nbt = worldState.storedPets.get(i);
				if (getUuid().equals(nbt.getUuid("Owner"))) {
					LivingEntity pet = (LivingEntity) Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("id"))).create(world);
					if (pet != null) {
						pet.fromTag(nbt);
						pet.setWorld(world);
						pet.teleport(getX() + 0.5, getY() + 0.5, getZ() + 0.5);
						pet.removed = false;
						pet.setHealth(pet.getMaxHealth());
						pet.extinguish();
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
	
	private static PlayerEntity findPlayer(World world, UUID uuid) {
		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			PlayerEntity player = serverWorld.getPlayerByUuid(uuid);
			if (player != null) {
				return player;
			}
		}
		return null;
	}
	
	private static boolean isPetRespawnable(RPWorldState worldState, Entity entity) {
		for (UUID uuid : worldState.petsToRespawn) {
			if (entity.getUuid().equals(uuid)) {
				return true;
			}
		}
		return false;
	}
}

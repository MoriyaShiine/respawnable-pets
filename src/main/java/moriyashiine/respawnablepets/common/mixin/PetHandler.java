package moriyashiine.respawnablepets.common.mixin;

import moriyashiine.respawnablepets.client.network.message.SmokePuffMessage;
import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.world.RPWorldState;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(LivingEntity.class)
public class PetHandler {
	private static final Tag<EntityType<?>> BLACKLIST = TagRegistry.entityType(new Identifier(RespawnablePets.MODID, "blacklisted"));
	
	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void toggleRespawn(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
		Object obj = this;
		//noinspection ConstantConditions
		if (obj instanceof LivingEntity) {
			LivingEntity thisObj = (LivingEntity) obj;
			World world = thisObj.world;
			if (!world.isClient) {
				Entity attacker = source.getSource();
				if (attacker instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) attacker;
					if (player.getStackInHand(Hand.MAIN_HAND).getItem() == RespawnablePets.etheric_gem) {
						CompoundTag stored = thisObj.toTag(new CompoundTag());
						if (stored.containsUuid("Owner") && player.getUuid().equals(stored.getUuid("Owner"))) {
							if (BLACKLIST.contains(thisObj.getType())) {
								player.sendMessage(new TranslatableText("message." + RespawnablePets.MODID + ".blacklisted", thisObj.getDisplayName()), true);
							}
							else {
								RPWorldState rpWorldState = RPWorldState.get(world);
								if (isPetRespawnable(rpWorldState, thisObj)) {
									player.sendMessage(new TranslatableText("message." + RespawnablePets.MODID + ".disable_respawn", thisObj.getDisplayName()), true);
									for (int i = rpWorldState.petsToRespawn.size() - 1; i >= 0; i--) {
										if (rpWorldState.petsToRespawn.get(i).equals(thisObj.getUuid())) {
											rpWorldState.petsToRespawn.remove(i);
											rpWorldState.markDirty();
										}
									}
								}
								else {
									player.sendMessage(new TranslatableText("message." + RespawnablePets.MODID + ".enable_respawn", thisObj.getDisplayName()), true);
									rpWorldState.petsToRespawn.add(thisObj.getUuid());
									rpWorldState.markDirty();
								}
								
							}
						}
						else {
							player.sendMessage(new TranslatableText("message." + RespawnablePets.MODID + ".not_owner", thisObj.getDisplayName()), true);
						}
						callbackInfo.cancel();
					}
				}
			}
		}
	}
	
	@Inject(method = "applyDamage", at = @At("HEAD"), cancellable = true)
	private void storeToWorld(DamageSource source, float amount, CallbackInfo callbackInfo) {
		Object obj = this;
		//noinspection ConstantConditions
		if (obj instanceof LivingEntity) {
			LivingEntity thisObj = (LivingEntity) obj;
			World world = thisObj.world;
			if (!world.isClient) {
				RPWorldState rpWorldState = RPWorldState.get(world);
				if (thisObj.getHealth() - amount <= 0 && isPetRespawnable(rpWorldState, thisObj)) {
					CompoundTag stored = new CompoundTag();
					thisObj.saveSelfToTag(stored);
					rpWorldState.storedPets.add(stored);
					rpWorldState.markDirty();
					BlockPos pos = thisObj.getBlockPos();
					PlayerStream.around(world, pos, 32).forEach(foundPlayer -> SmokePuffMessage.send(foundPlayer, thisObj.getEntityId()));
					world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1, 1);
					thisObj.remove();
					PlayerEntity owner = findPlayer(world, stored.getUuid("Owner"));
					if (owner != null && world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
						owner.sendMessage(thisObj.getDamageTracker().getDeathMessage(), false);
					}
					callbackInfo.cancel();
				}
			}
		}
	}
	
	@Inject(method = "wakeUp", at = @At("HEAD"))
	private void respawnPets(CallbackInfo callbackInfo) {
		Object obj = this;
		//noinspection ConstantConditions
		if (obj instanceof LivingEntity) {
			LivingEntity thisObj = (LivingEntity) obj;
			World world = thisObj.world;
			if (!world.isClient) {
				RPWorldState rpWorldState = RPWorldState.get(world);
				for (int i = rpWorldState.storedPets.size() - 1; i >= 0; i--) {
					CompoundTag nbt = rpWorldState.storedPets.get(i);
					if (thisObj.getUuid().equals(nbt.getUuid("Owner"))) {
						LivingEntity pet = (LivingEntity) Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("id"))).create(world);
						pet.fromTag(nbt);
						pet.setWorld(world);
						pet.teleport(thisObj.getX() + 0.5, thisObj.getY() + 0.5, thisObj.getZ() + 0.5);
						pet.removed = false;
						pet.setHealth(pet.getMaxHealth());
						pet.extinguish();
						pet.clearStatusEffects();
						pet.fallDistance = 0;
						world.spawnEntity(pet);
						rpWorldState.storedPets.remove(i);
						rpWorldState.markDirty();
					}
				}
			}
		}
	}
	
	private static PlayerEntity findPlayer(World world, UUID uuid) {
		for (ServerWorld serverWorld : Objects.requireNonNull(world.getServer()).getWorlds()) {
			PlayerEntity player = serverWorld.getPlayerByUuid(uuid);
			if (player != null) {
				return player;
			}
		}
		return null;
	}
	
	private static boolean isPetRespawnable(RPWorldState rpWorldState, LivingEntity entity) {
		for (UUID uuid : rpWorldState.petsToRespawn) {
			if (entity.getUuid().equals(uuid)) {
				return true;
			}
		}
		return false;
	}
}
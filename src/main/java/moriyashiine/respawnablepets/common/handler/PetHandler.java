package moriyashiine.respawnablepets.common.handler;

import moriyashiine.respawnablepets.RespawnablePets;
import moriyashiine.respawnablepets.common.network.SmokePuffMessage;
import moriyashiine.respawnablepets.common.registry.RPItems;
import moriyashiine.respawnablepets.common.world.RPWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class PetHandler {
	private static final ITeleporter NO_PORTAL = new ITeleporter() {
		@Override
		public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
			return repositionEntity.apply(false);
		}
	};
	
	private static final EntityTypeTags.Wrapper BLACKLISTED = new EntityTypeTags.Wrapper(new ResourceLocation(RespawnablePets.MODID, "blacklisted"));
	
	@SubscribeEvent
	public void togglePetRespawn(LivingAttackEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote && !(entity instanceof PlayerEntity)) {
			Entity attacker = event.getSource().getImmediateSource();
			if (attacker instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) attacker;
				if (player.getHeldItemMainhand().getItem() == RPItems.etheric_gem) {
					event.setCanceled(true);
					if (entity.serializeNBT().getString("OwnerUUID").equals(player.getUniqueID().toString())) {
						if (BLACKLISTED.contains(entity.getType())) {
							player.sendStatusMessage(new TranslationTextComponent("message." + RespawnablePets.MODID + ".blacklisted", entity.getDisplayName()), true);
						}
						else {
							RPWorld rpworld = RPWorld.get(world);
							if (isPetRespawnable(rpworld, entity)) {
								player.sendStatusMessage(new TranslationTextComponent("message." + RespawnablePets.MODID + ".disable_respawn", entity.getDisplayName()), true);
								for (int i = rpworld.petsToRespawn.size() - 1; i >= 0; i--) {
									if (rpworld.petsToRespawn.get(i).equals(entity.getUniqueID())) {
										rpworld.petsToRespawn.remove(i);
										rpworld.markDirty();
									}
								}
							}
							else {
								player.sendStatusMessage(new TranslationTextComponent("message." + RespawnablePets.MODID + ".enable_respawn", entity.getDisplayName()), true);
								rpworld.petsToRespawn.add(entity.getUniqueID());
								rpworld.markDirty();
							}
						}
					}
					else {
						player.sendStatusMessage(new TranslationTextComponent("message." + RespawnablePets.MODID + ".not_owner", entity.getDisplayName()), true);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public void respawnPets(PlayerWakeUpEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = player.world;
		if (!world.isRemote) {
			RPWorld rpworld = RPWorld.get(world);
			for (int i = rpworld.storedPets.size() - 1; i >= 0; i--) {
				CompoundNBT nbt = rpworld.storedPets.get(i);
				if (player.getUniqueID().equals(UUID.fromString(nbt.getString("OwnerUUID")))) {
					LivingEntity pet = (LivingEntity) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("id")))).create(world);
					if (pet != null) {
						pet.deserializeNBT(nbt);
						BlockPos playerPos = player.getPosition();
						pet.changeDimension(player.dimension, NO_PORTAL);
						pet.setPositionAndRotation(playerPos.getX(), playerPos.getY(), playerPos.getZ(), world.rand.nextInt(360), 0);
						pet.removed = false;
						pet.heal(Float.MAX_VALUE);
						pet.extinguish();
						pet.clearActivePotions();
						pet.fallDistance = 0;
						world.addEntity(pet);
						rpworld.storedPets.remove(i);
						rpworld.markDirty();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void storePet(LivingDamageEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.world;
		if (!world.isRemote && !(entity instanceof PlayerEntity)) {
			RPWorld rpworld = RPWorld.get(world);
			if (entity.getHealth() - event.getAmount() <= 0 && isPetRespawnable(rpworld, entity)) {
				event.setCanceled(true);
				rpworld.storedPets.add(entity.serializeNBT());
				rpworld.markDirty();
				RespawnablePets.NETWORK_CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(entity.getPosX(), entity.getPosY(), entity.getPosZ(), 32, entity.dimension)), new SmokePuffMessage(entity.getEntityId()));
				world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1, 1);
				entity.remove();
				PlayerEntity owner = findPlayer(world, UUID.fromString(entity.serializeNBT().getString("OwnerUUID")));
				if (owner != null && world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
					owner.sendMessage(entity.getCombatTracker().getDeathMessage());
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
	
	private static boolean isPetRespawnable(RPWorld rpworld, LivingEntity entity) {
		for (UUID uuid : rpworld.petsToRespawn) {
			if (entity.getUniqueID().equals(uuid)) {
				return true;
			}
		}
		return false;
	}
}
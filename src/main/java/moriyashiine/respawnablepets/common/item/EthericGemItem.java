/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.item;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.init.ModCriterion;
import moriyashiine.respawnablepets.common.init.ModEntityComponents;
import moriyashiine.respawnablepets.common.init.ModEntityTypeTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class EthericGemItem extends Item {
	public EthericGemItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (user.isSneaking()) {
			List<MobEntity> entities = world.getEntitiesByClass(MobEntity.class, new Box(user.getBlockPos()).expand(9, 3, 9), foundEntity -> {
				if (!ModEntityComponents.RESPAWNABLE.get(foundEntity).isRespawnable()) {
					return foundEntity instanceof Tameable tameable && tameable.getOwner() == user;
				}
				return false;
			});
			if (!entities.isEmpty()) {
				if (user instanceof ServerPlayerEntity serverPlayer) {
					ModCriterion.MAKE_PET_RESPAWNABLE.trigger(serverPlayer);
				}
				entities.forEach(entity -> ModEntityComponents.RESPAWNABLE.get(entity).setRespawnable(true));
				if (entities.size() == 1) {
					user.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.enable_respawn", entities.getFirst().getDisplayName()), true);
				} else {
					user.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.enable_respawn", Text.translatable("respawnablepets.message.counted_entities", entities.size())), true);
				}
				return ActionResult.SUCCESS;
			}
		}
		return super.use(world, user, hand);
	}

	public static ActionResult useOnEntity(PlayerEntity user, LivingEntity entity) {
		if (entity instanceof Tameable tameable && tameable.getOwner() == user) {
			if (entity.getType().isIn(ModEntityTypeTags.CANNOT_RESPAWN)) {
				user.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.cannot_respawn", entity.getDisplayName()), true);
			} else {
				RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.get(entity);
				if (!respawnableComponent.isRespawnable() && user instanceof ServerPlayerEntity serverPlayer) {
					ModCriterion.MAKE_PET_RESPAWNABLE.trigger(serverPlayer);
				}
				user.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message." + (respawnableComponent.isRespawnable() ? "disable" : "enable") + "_respawn", entity.getDisplayName()), true);
				respawnableComponent.setRespawnable(!respawnableComponent.isRespawnable());
			}
		} else {
			user.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.not_owner", entity.getDisplayName()), true);
		}
		return ActionResult.SUCCESS;
	}
}

/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.world.item;

import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.init.RespawnablePetsEntityComponents;
import moriyashiine.respawnablepets.common.init.RespawnablePetsTriggers;
import moriyashiine.respawnablepets.common.tag.RespawnablePetsEntityTypeTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EthericGemItem extends Item {
	public EthericGemItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.isShiftKeyDown()) {
			List<Mob> entities = level.getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(9, 3, 9), foundEntity -> !foundEntity.is(RespawnablePetsEntityTypeTags.CANNOT_RESPAWN) && !RespawnablePetsEntityComponents.RESPAWNABLE.get(foundEntity).isRespawnable() && foundEntity instanceof OwnableEntity ownable && ownable.getOwner() == player);
			if (!entities.isEmpty()) {
				if (player instanceof ServerPlayer serverPlayer) {
					RespawnablePetsTriggers.MAKE_PET_RESPAWNABLE.trigger(serverPlayer);
				}
				entities.forEach(entity -> RespawnablePetsEntityComponents.RESPAWNABLE.get(entity).setRespawnable(true));
				if (entities.size() == 1) {
					player.sendOverlayMessage(Component.translatable("respawnable-pets.message.enable_respawn", entities.getFirst().getDisplayName()));
				} else {
					player.sendOverlayMessage(Component.translatable("respawnable-pets.message.enable_respawn", Component.translatable("respawnable-pets.message.counted_entities", entities.size())));
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(level, player, hand);
	}

	public static InteractionResult useOnEntity(Player user, LivingEntity entity) {
		if (entity instanceof OwnableEntity ownable && ownable.getOwner() == user) {
			if (entity.is(RespawnablePetsEntityTypeTags.CANNOT_RESPAWN)) {
				user.sendOverlayMessage(Component.translatable("respawnable-pets.message.cannot_respawn", entity.getDisplayName()));
				return InteractionResult.FAIL;
			}
			RespawnableComponent respawnable = RespawnablePetsEntityComponents.RESPAWNABLE.get(entity);
			if (!respawnable.isRespawnable() && user instanceof ServerPlayer player) {
				RespawnablePetsTriggers.MAKE_PET_RESPAWNABLE.trigger(player);
			}
			user.sendOverlayMessage(Component.translatable(respawnable.isRespawnable() ? "respawnable-pets.message.disable_respawn" : "respawnable-pets.message.enable_respawn", entity.getDisplayName()));
			respawnable.setRespawnable(!respawnable.isRespawnable());
			return InteractionResult.SUCCESS;
		}
		user.sendOverlayMessage(Component.translatable("respawnable-pets.message.not_owner", entity.getDisplayName()));
		return InteractionResult.FAIL;
	}
}

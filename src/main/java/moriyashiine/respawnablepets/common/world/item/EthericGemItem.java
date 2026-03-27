/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.world.item;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.init.ModEntityComponents;
import moriyashiine.respawnablepets.common.init.ModTriggers;
import moriyashiine.respawnablepets.common.tag.ModEntityTypeTags;
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
	public EthericGemItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.isShiftKeyDown()) {
			List<Mob> entities = level.getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(9, 3, 9), foundEntity -> !foundEntity.is(ModEntityTypeTags.CANNOT_RESPAWN) && !ModEntityComponents.RESPAWNABLE.get(foundEntity).isRespawnable() && foundEntity instanceof OwnableEntity ownable && ownable.getOwner() == player);
			if (!entities.isEmpty()) {
				if (player instanceof ServerPlayer serverPlayer) {
					ModTriggers.MAKE_PET_RESPAWNABLE.trigger(serverPlayer);
				}
				entities.forEach(entity -> ModEntityComponents.RESPAWNABLE.get(entity).setRespawnable(true));
				if (entities.size() == 1) {
					player.sendOverlayMessage(Component.translatable(RespawnablePets.MOD_ID + ".message.enable_respawn", entities.getFirst().getDisplayName()));
				} else {
					player.sendOverlayMessage(Component.translatable(RespawnablePets.MOD_ID + ".message.enable_respawn", Component.translatable("respawnablepets.message.counted_entities", entities.size())));
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(level, player, hand);
	}

	public static InteractionResult useOnEntity(Player user, LivingEntity entity) {
		if (entity instanceof OwnableEntity tameable && tameable.getOwner() == user) {
			if (entity.is(ModEntityTypeTags.CANNOT_RESPAWN)) {
				user.sendOverlayMessage(Component.translatable(RespawnablePets.MOD_ID + ".message.cannot_respawn", entity.getDisplayName()));
				return InteractionResult.FAIL;
			}
			RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.get(entity);
			if (!respawnableComponent.isRespawnable() && user instanceof ServerPlayer player) {
				ModTriggers.MAKE_PET_RESPAWNABLE.trigger(player);
			}
			user.sendOverlayMessage(Component.translatable(RespawnablePets.MOD_ID + ".message." + (respawnableComponent.isRespawnable() ? "disable" : "enable") + "_respawn", entity.getDisplayName()));
			respawnableComponent.setRespawnable(!respawnableComponent.isRespawnable());
			return InteractionResult.SUCCESS;
		}
		user.sendOverlayMessage(Component.translatable(RespawnablePets.MOD_ID + ".message.not_owner", entity.getDisplayName()));
		return InteractionResult.FAIL;
	}
}

/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.item;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.component.entity.RespawnableComponent;
import moriyashiine.respawnablepets.common.registry.ModCriterion;
import moriyashiine.respawnablepets.common.registry.ModEntityComponents;
import moriyashiine.respawnablepets.common.registry.ModEntityTypeTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class EthericGemItem extends Item {
	public EthericGemItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isSneaking()) {
			if (!world.isClient) {
				List<MobEntity> entities = world.getEntitiesByClass(MobEntity.class, new Box(user.getBlockPos()).expand(9, 3, 9), foundEntity -> {
					if (!ModEntityComponents.RESPAWNABLE.get(foundEntity).getRespawnable()) {
						return foundEntity instanceof Tameable tameable && user.getUuid().equals(tameable.getOwnerUuid());
					}
					return false;
				});
				if (!entities.isEmpty()) {
					ModCriterion.MAKE_PET_RESPAWNABLE.trigger((ServerPlayerEntity) user);
					entities.forEach(entity -> {
						RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.get(entity);
						respawnableComponent.setRespawnable(true);
						respawnableComponent.sync();
					});
					if (entities.size() == 1) {
						user.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.enable_respawn", entities.get(0).getDisplayName()), true);
					} else {
						user.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.enable_respawn", Text.translatable("respawnablepets.message.counted_entities", entities.size())), true);
					}
				}
			}
			return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
		}
		return super.use(world, user, hand);
	}

	public static ActionResult useOnEntity(PlayerEntity player, LivingEntity entity) {
		if (!player.getWorld().isClient) {
			if (entity instanceof Tameable tameable && player.getUuid().equals(tameable.getOwnerUuid())) {
				if (entity.getType().isIn(ModEntityTypeTags.CANNOT_RESPAWN)) {
					player.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.cannot_respawn", entity.getDisplayName()), true);
				} else {
					RespawnableComponent respawnableComponent = ModEntityComponents.RESPAWNABLE.get(entity);
					if (respawnableComponent.getRespawnable()) {
						player.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.disable_respawn", entity.getDisplayName()), true);
						respawnableComponent.setRespawnable(false);
					} else {
						player.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.enable_respawn", entity.getDisplayName()), true);
						respawnableComponent.setRespawnable(true);
						ModCriterion.MAKE_PET_RESPAWNABLE.trigger((ServerPlayerEntity) player);
					}
					respawnableComponent.sync();
				}
			} else {
				player.sendMessage(Text.translatable(RespawnablePets.MOD_ID + ".message.not_owner", entity.getDisplayName()), true);
			}
		}
		return ActionResult.success(player.getWorld().isClient);
	}
}

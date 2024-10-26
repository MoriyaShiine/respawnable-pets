/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.respawnablepets.common.init.ModItems;
import moriyashiine.respawnablepets.common.item.EthericGemItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
	protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyReturnValue(method = "interactWithItem", at = @At("RETURN"))
	private ActionResult respawnablepets$toggleRespawnStatus(ActionResult original, PlayerEntity player, Hand hand) {
		if (original == ActionResult.PASS && player.getStackInHand(hand).isOf(ModItems.ETHERIC_GEM)) {
			return EthericGemItem.useOnEntity(player, this);
		}
		return original;
	}
}

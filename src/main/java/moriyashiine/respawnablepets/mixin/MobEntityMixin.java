/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.mixin;

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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
	protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "interactWithItem", at = @At("TAIL"), cancellable = true)
	private void respawnablepets$toggleRespawnStatus(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (cir.getReturnValue() == ActionResult.PASS && player.getStackInHand(hand).isOf(ModItems.ETHERIC_GEM)) {
			cir.setReturnValue(EthericGemItem.useOnEntity(player, this));
		}
	}
}

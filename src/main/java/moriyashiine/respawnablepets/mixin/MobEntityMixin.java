/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.mixin;

import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.registry.ModComponents;
import moriyashiine.respawnablepets.common.registry.ModEntityTypeTags;
import moriyashiine.respawnablepets.common.registry.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
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
			if (!world.isClient) {
				NbtCompound compound = writeNbt(new NbtCompound());
				if (compound.containsUuid("Owner") && player.getUuid().equals(compound.getUuid("Owner"))) {
					if (getType().isIn(ModEntityTypeTags.BLACKLISTED)) {
						player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.blacklisted", getDisplayName()), true);
					} else {
						ModComponents.RESPAWNABLE.maybeGet(this).ifPresent(respawnableComponent -> {
							if (respawnableComponent.getRespawnable()) {
								player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.disable_respawn", getDisplayName()), true);
								respawnableComponent.setRespawnable(false);
							} else {
								player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.enable_respawn", getDisplayName()), true);
								respawnableComponent.setRespawnable(true);
							}
						});
					}
				} else {
					player.sendMessage(new TranslatableText(RespawnablePets.MOD_ID + ".message.not_owner", getDisplayName()), true);
				}
			}
			cir.setReturnValue(ActionResult.success(world.isClient));
		}
	}
}

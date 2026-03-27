/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.respawnablepets.common.init.ModItems;
import moriyashiine.respawnablepets.common.world.item.EthericGemItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {
	@ModifyReturnValue(method = "checkAndHandleImportantInteractions", at = @At("RETURN"))
	private InteractionResult respawnablepets$toggleRespawnStatus(InteractionResult original, Player player, InteractionHand hand) {
		if (original == InteractionResult.PASS && player.getItemInHand(hand).is(ModItems.ETHERIC_GEM)) {
			return EthericGemItem.useOnEntity(player, (Mob) (Object) this);
		}
		return original;
	}
}

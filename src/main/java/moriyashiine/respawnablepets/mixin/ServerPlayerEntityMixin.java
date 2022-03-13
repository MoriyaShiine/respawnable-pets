/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.mixin;

import com.mojang.authlib.GameProfile;
import moriyashiine.respawnablepets.common.RespawnablePets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void respawnablepets$respawnPetsOnTick(CallbackInfo ci) {
		int timeToRespawn = RespawnablePets.config.timeToRespawn;
		if (timeToRespawn >= 0 && world.getTimeOfDay() % 24000 == timeToRespawn) {
			RespawnablePets.respawnPets(this);
		}
	}
}

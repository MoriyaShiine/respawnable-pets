package moriyashiine.respawnablepets.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.respawnablepets.common.RespawnablePets;
import moriyashiine.respawnablepets.common.RespawnablePetsConfig;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Entity {
	@Unique
	private static final AttachmentType<EntityReference<LivingEntity>> OWNER = AttachmentRegistry.create(RespawnablePets.id("owner"), builder -> builder
			.persistent(EntityReference.codec())
			.syncWith(EntityReference.streamCodec(), AttachmentSyncPredicate.allButTarget())
	);

	@Shadow
	public abstract boolean tameWithName(Player player);

	@Shadow
	private @Nullable EntityReference<LivingEntity> owner;

	public AbstractHorseMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void respawnablepets$ownerFix(ValueInput input, CallbackInfo ci) {
		setAttached(OWNER, owner);
	}

	@ModifyReturnValue(method = "getOwnerReference", at = @At("RETURN"))
	private EntityReference<LivingEntity> respawnablepets$ownerFix(@Nullable EntityReference<LivingEntity> original) {
		return getAttachedOrElse(OWNER, owner);
	}

	@Inject(method = "setOwner", at = @At("TAIL"))
	private void respawnablepets$ownerFix(LivingEntity owner, CallbackInfo ci) {
		setAttached(OWNER, EntityReference.of(owner));
	}

	@Inject(method = "doPlayerRide", at = @At("TAIL"))
	private void respawnablepets$tameRidden(Player player, CallbackInfo ci) {
		if (owner == null && isValid(getType())) {
			tameWithName(player);
		}
	}

	@Unique
	private static boolean isValid(EntityType<?> type) {
		return RespawnablePetsConfig.tameWhenMounted.contains(type.builtInRegistryHolder().key().identifier().toString());
	}
}

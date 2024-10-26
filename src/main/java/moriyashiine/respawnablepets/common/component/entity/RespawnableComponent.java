/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.component.entity;

import moriyashiine.respawnablepets.common.init.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;

public class RespawnableComponent implements AutoSyncedComponent, ClientTickingComponent {
	private final MobEntity obj;
	private boolean respawnable = false;

	public RespawnableComponent(MobEntity obj) {
		this.obj = obj;
	}

	@Override
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		respawnable = tag.getBoolean("Respawnable");
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putBoolean("Respawnable", respawnable);
	}

	public boolean isRespawnable() {
		return respawnable;
	}

	public void setRespawnable(boolean respawnable) {
		this.respawnable = respawnable;
	}

	@Override
	public void clientTick() {
		if (respawnable && obj.age % 20 == 0 && MinecraftClient.getInstance().getCameraEntity() instanceof LivingEntity living && living.isHolding(ModItems.ETHERIC_GEM) && obj instanceof Tameable tameable && tameable.getOwner() == living) {
			for (int i = 0; i < 16; i++) {
				obj.getWorld().addParticle(ParticleTypes.GLOW, obj.getParticleX(1), obj.getRandomBodyY(), obj.getParticleZ(1), 0, 0, 0);
			}
		}
	}
}

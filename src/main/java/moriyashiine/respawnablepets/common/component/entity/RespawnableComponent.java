/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.component.entity;

import moriyashiine.respawnablepets.common.init.ModItems;
import moriyashiine.strawberrylib.api.module.SLibClientUtils;
import moriyashiine.strawberrylib.api.objects.enums.ParticleAnchor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;

public class RespawnableComponent implements AutoSyncedComponent, ClientTickingComponent {
	private final MobEntity obj;
	private boolean respawnable = false;

	public RespawnableComponent(MobEntity obj) {
		this.obj = obj;
	}

	@Override
	public void readData(ReadView readView) {
		respawnable = readView.getBoolean("Respawnable", false);
	}

	@Override
	public void writeData(WriteView writeView) {
		writeView.putBoolean("Respawnable", respawnable);
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
			SLibClientUtils.addParticles(obj, ParticleTypes.GLOW, 16, ParticleAnchor.BODY);
		}
	}
}

/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.component.entity;

import moriyashiine.respawnablepets.common.init.ModItems;
import moriyashiine.strawberrylib.api.module.SLibClientUtils;
import moriyashiine.strawberrylib.api.objects.enums.ParticleAnchor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;

public class RespawnableComponent implements AutoSyncedComponent, ClientTickingComponent {
	private final Mob obj;
	private boolean respawnable = false;

	public RespawnableComponent(Mob obj) {
		this.obj = obj;
	}

	@Override
	public void readData(ValueInput input) {
		respawnable = input.getBooleanOr("Respawnable", false);
	}

	@Override
	public void writeData(ValueOutput output) {
		output.putBoolean("Respawnable", respawnable);
	}

	public boolean isRespawnable() {
		return respawnable;
	}

	public void setRespawnable(boolean respawnable) {
		this.respawnable = respawnable;
	}

	@Override
	public void clientTick() {
		if (respawnable && obj.tickCount % 20 == 0 && Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living && living.isHolding(ModItems.ETHERIC_GEM) && obj instanceof OwnableEntity ownable && ownable.getOwner() == living) {
			SLibClientUtils.addParticles(obj, ParticleTypes.GLOW, 16, ParticleAnchor.BODY);
		}
	}
}

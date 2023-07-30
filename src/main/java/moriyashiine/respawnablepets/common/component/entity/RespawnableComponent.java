/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.respawnablepets.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import moriyashiine.respawnablepets.common.init.ModEntityComponents;
import moriyashiine.respawnablepets.common.init.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;

public class RespawnableComponent implements AutoSyncedComponent, ClientTickingComponent {
	private final MobEntity obj;
	private boolean respawnable = false;

	public RespawnableComponent(MobEntity obj) {
		this.obj = obj;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		respawnable = tag.getBoolean("Respawnable");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putBoolean("Respawnable", respawnable);
	}

	public void sync() {
		obj.syncComponent(ModEntityComponents.RESPAWNABLE);
	}

	public boolean getRespawnable() {
		return respawnable;
	}

	public void setRespawnable(boolean respawnable) {
		this.respawnable = respawnable;
	}

	@Override
	public void clientTick() {
		if (respawnable && obj.age % 20 == 0 && MinecraftClient.getInstance().cameraEntity instanceof LivingEntity living) {
			if (living.getMainHandStack().isOf(ModItems.ETHERIC_GEM) || living.getOffHandStack().isOf(ModItems.ETHERIC_GEM)) {
				if (obj instanceof Tameable tameable && living.getUuid().equals(tameable.getOwnerUuid())) {
					for (int i = 0; i < 16; i++) {
						obj.getWorld().addParticle(ParticleTypes.GLOW, obj.getParticleX(1), obj.getRandomBodyY(), obj.getParticleZ(1), 0, 0, 0);
					}
				}
			}
		}
	}
}

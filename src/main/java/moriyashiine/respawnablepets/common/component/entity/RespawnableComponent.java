/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import moriyashiine.respawnablepets.common.registry.ModEntityComponents;
import moriyashiine.respawnablepets.common.registry.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
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
				NbtCompound stored = obj.writeNbt(new NbtCompound());
				if (stored.containsUuid("Owner") && living.getUuid().equals(stored.getUuid("Owner"))) {
					for (int i = 0; i < 16; i++) {
						obj.world.addParticle(ParticleTypes.GLOW, obj.getParticleX(1), obj.getRandomBodyY(), obj.getParticleZ(1), 0, 0, 0);
					}
				}
			}
		}
	}
}

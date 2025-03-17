/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.respawnablepets.common.event.RespawnPetsEvent;
import moriyashiine.respawnablepets.common.event.StorePetEvent;
import moriyashiine.respawnablepets.common.init.ModCriterion;
import moriyashiine.respawnablepets.common.init.ModItems;
import moriyashiine.respawnablepets.common.init.ModSoundEvents;
import moriyashiine.strawberrylib.api.SLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;

public class RespawnablePets implements ModInitializer {
	public static final String MOD_ID = "respawnablepets";

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, ModConfig.class);
		SLib.init(MOD_ID);
		initRegistries();
		initEvents();
	}

	public static Identifier id(String value) {
		return Identifier.of(MOD_ID, value);
	}

	private void initRegistries() {
		ModItems.init();
		ModSoundEvents.init();
		ModCriterion.init();
	}

	private void initEvents() {
		ServerLivingEntityEvents.ALLOW_DEATH.register(new StorePetEvent());
		EntitySleepEvents.STOP_SLEEPING.register(new RespawnPetsEvent.Sleep());
		ServerTickEvents.END_SERVER_TICK.register(new RespawnPetsEvent.Tick());
	}
}

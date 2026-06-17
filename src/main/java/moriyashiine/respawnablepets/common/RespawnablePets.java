/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common;

import eu.midnightdust.lib.config.MidnightConfig;
import moriyashiine.respawnablepets.common.event.RespawnPetsEvent;
import moriyashiine.respawnablepets.common.event.StorePetEvent;
import moriyashiine.respawnablepets.common.init.RespawnablePetsItems;
import moriyashiine.respawnablepets.common.init.RespawnablePetsSoundEvents;
import moriyashiine.respawnablepets.common.init.RespawnablePetsTriggers;
import moriyashiine.strawberrylib.api.SLib;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class RespawnablePets implements ModInitializer {
	public static final String MOD_ID = "respawnable-pets";

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, RespawnablePetsConfig.class);
		SLib.init(MOD_ID);
		initRegistries();
		initEvents();
	}

	public static Identifier id(String value) {
		return Identifier.fromNamespaceAndPath(MOD_ID, value);
	}

	private void initRegistries() {
		RespawnablePetsItems.init();
		RespawnablePetsTriggers.init();
		RespawnablePetsSoundEvents.init();
	}

	private void initEvents() {
		RespawnPetsEvent.init();
		StorePetEvent.init();
	}
}

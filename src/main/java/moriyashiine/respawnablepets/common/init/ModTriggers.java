/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */

package moriyashiine.respawnablepets.common.init;

import net.minecraft.advancements.criterion.PlayerTrigger;

import static moriyashiine.strawberrylib.api.module.SLibRegistries.registerTrigger;

public class ModTriggers {
	public static final PlayerTrigger MAKE_PET_RESPAWNABLE = registerTrigger("make_pet_respawnable", new PlayerTrigger());

	public static void init() {
	}
}

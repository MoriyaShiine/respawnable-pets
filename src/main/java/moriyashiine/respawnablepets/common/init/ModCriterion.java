/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.init;

import net.minecraft.advancement.criterion.TickCriterion;

import static moriyashiine.strawberrylib.api.module.SLibRegistries.registerCriterion;

public class ModCriterion {
	public static TickCriterion MAKE_PET_RESPAWNABLE = registerCriterion("make_pet_respawnable", new TickCriterion());

	public static void init() {
	}
}

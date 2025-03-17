/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common.init;

import net.minecraft.sound.SoundEvent;

import static moriyashiine.strawberrylib.api.module.SLibRegistries.registerSoundEvent;

public class ModSoundEvents {
	public static final SoundEvent ENTITY_GENERIC_TELEPORT = registerSoundEvent("entity.generic.teleport");

	public static void init() {
	}
}

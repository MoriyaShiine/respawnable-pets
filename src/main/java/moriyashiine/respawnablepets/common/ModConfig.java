/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.respawnablepets.common;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {
	@Entry
	public static boolean respawnAfterSleep = true;

	@Entry(min = -1, max = 23999, isSlider = true)
	public static int timeOfDayToRespawn = -1;
}

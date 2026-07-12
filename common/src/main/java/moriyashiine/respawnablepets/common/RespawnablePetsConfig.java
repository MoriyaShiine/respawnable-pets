package moriyashiine.respawnablepets.common;

import eu.midnightdust.lib.config.MidnightConfig;

import java.util.Arrays;
import java.util.List;

public class RespawnablePetsConfig extends MidnightConfig {
	@Entry
	public static List<String> tameWhenMounted = Arrays.asList(
			"minecraft:camel",
			"minecraft:camel_husk",
			"minecraft:skeleton_horse"
	);

	@Entry
	public static boolean respawnAfterSleep = true;

	@Entry(min = -1, max = 23999, isSlider = true)
	public static int timeOfDayToRespawn = -1;
}

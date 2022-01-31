/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.respawnablepets.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = RespawnablePets.MOD_ID)
public class ModConfig implements ConfigData {
	public int timeToRespawn = -1;
}

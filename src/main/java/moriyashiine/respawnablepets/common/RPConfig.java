package moriyashiine.respawnablepets.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = RespawnablePets.MODID)
public class RPConfig implements ConfigData {
	public int timeToRespawn = -1;
}

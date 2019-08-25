package moriyashiine.respawnablepets;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

class ModConfig extends Configuration {
	final List<String> blacklist;
	
	ModConfig(File file) {
		super(file);
		load();
		blacklist = Arrays.asList(getStringList("blacklist", "entity", new String[]{}, "Entities listed here will not be allowed to respawn. Example: 'minecraft:wolf'"));
		save();
	}
}
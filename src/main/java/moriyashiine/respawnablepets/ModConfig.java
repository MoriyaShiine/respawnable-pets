package moriyashiine.respawnablepets;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

@SuppressWarnings("WeakerAccess")
public class ModConfig extends Configuration {
	public final String[] blacklist;
	
	public ModConfig(File file) {
		super(file);
		load();
		blacklist = getStringList("blacklist", "entity", new String[]{}, "Entities listed here will be excluded from respawning. Example: 'minecraft:wolf'");
		save();
	}
}
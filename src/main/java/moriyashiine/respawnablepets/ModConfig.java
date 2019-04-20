package moriyashiine.respawnablepets;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ModConfig extends Configuration
{
	public final String[] blacklist;
	public final String[] name_blacklist;
	
	public ModConfig(File file)
	{
		super(file);
		load();
		blacklist = getStringList("blacklist", "entity", new String[] {}, "Entities listed here will be excluded from respawning. Example: 'minecraft:wolf'");
		name_blacklist = getStringList("name_blacklist", "entity", new String[] {"$delete"}, "Entities named one of these (via name tag) will be excluded from respawning.");
		save();
	}
}
package moriyashiine.respawnablepets;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ModConfig extends Configuration
{
	public final String[] blacklist;
	
	public ModConfig(File file)
	{
		super(file);
		load();
		blacklist = getStringList("blacklist", "entity", new String[] {}, "Entities listed here will be excluded from respawning.");
		save();
	}
}
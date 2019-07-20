package moriyashiine.respawnablepets;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

class Config {
	static final Config COMMON;
	static final ForgeConfigSpec COMMON_SPEC;
	
	static {
		final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
	
	final ForgeConfigSpec.ConfigValue<List<String>> blacklist;
	
	private Config(ForgeConfigSpec.Builder builder) {
		blacklist = builder.comment("Entities listed here will be excluded from respawning. Example: 'minecraft:wolf'").define("core.blacklist", new ArrayList<>());
	}
}
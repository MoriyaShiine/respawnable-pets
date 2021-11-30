package moriyashiine.respawnablepets.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import moriyashiine.respawnablepets.common.registry.ModItems;
import moriyashiine.respawnablepets.common.registry.ModSoundEvents;
import net.fabricmc.api.ModInitializer;

public class RespawnablePets implements ModInitializer {
	public static final String MOD_ID = "respawnablepets";
	
	public static ModConfig config;
	
	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		ModItems.init();
		ModSoundEvents.init();
	}
}

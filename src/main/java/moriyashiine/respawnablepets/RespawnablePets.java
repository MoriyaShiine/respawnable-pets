package moriyashiine.respawnablepets;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = RespawnablePets.MODID, name = RespawnablePets.NAME, version = RespawnablePets.VERSION)
public class RespawnablePets
{
	public static final String MODID = "respawnablepets", NAME = "Respawnable Pets", VERSION = "1.0.2";
	
	public static ModConfig config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		config = new ModConfig(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}
}
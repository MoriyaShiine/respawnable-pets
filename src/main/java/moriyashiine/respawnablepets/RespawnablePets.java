package moriyashiine.respawnablepets;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod(modid = RespawnablePets.MODID, name = RespawnablePets.NAME, version = RespawnablePets.VERSION)
public class RespawnablePets {
	static final String MODID = "respawnablepets", NAME = "Respawnable Pets", VERSION = "1.0.5";
	
	@SidedProxy(serverSide = "moriyashiine.respawnablepets.ServerProxy", clientSide = "moriyashiine.respawnablepets.ClientProxy")
	static ServerProxy proxy;
	
	static ModConfig config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new ModConfig(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}
	
	@Mod.EventBusSubscriber
	static class Registry {
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			Item item = new ItemEthericGem();
			event.getRegistry().register(item);
			proxy.registerTexture(item);
		}
	}
}
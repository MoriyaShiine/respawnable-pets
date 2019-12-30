package moriyashiine.respawnablepets;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

@Mod(modid = RespawnablePets.MODID, name = RespawnablePets.NAME, version = RespawnablePets.VERSION)
public class RespawnablePets {
	static final String MODID = "respawnablepets", NAME = "Respawnable Pets", VERSION = "1.0.6";
	
	@SidedProxy(serverSide = "moriyashiine." + MODID + ".ServerProxy", clientSide = "moriyashiine." + MODID + ".ClientProxy")
	static ServerProxy proxy;
	
	static ModConfig config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new ModConfig(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}
	
	@Mod.EventBusSubscriber
	static class Registry {
		static final Item etheric_gem = new Item() {
			@Override
			public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
				tooltip.add(I18n.format(MODID + ".tooltip"));
			}
		}.setRegistryName("etheric_gem").setTranslationKey(MODID + ".etheric_gem").setCreativeTab(CreativeTabs.MISC).setMaxStackSize(1);
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().register(etheric_gem);
			proxy.registerTexture();
		}
	}
}
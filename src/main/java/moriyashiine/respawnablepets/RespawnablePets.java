package moriyashiine.respawnablepets;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.List;

@Mod(RespawnablePets.MODID)
public class RespawnablePets {
	static final String MODID = "respawnablepets";
	
	public RespawnablePets() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Registry {
		static final Item etheric_gem = new Item(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1).rarity(Rarity.RARE)) {
			@Override
			@OnlyIn(Dist.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
				tooltip.add(new TranslationTextComponent(MODID + ".tooltip").applyTextStyle(TextFormatting.GRAY));
			}
		}.setRegistryName("etheric_gem");
		
		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {
			event.getRegistry().register(etheric_gem);
		}
	}
}
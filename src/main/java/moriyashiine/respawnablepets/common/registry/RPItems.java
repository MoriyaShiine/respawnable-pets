package moriyashiine.respawnablepets.common.registry;

import moriyashiine.respawnablepets.RespawnablePets;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RPItems {
	public static final Item etheric_gem = new Item(new Item.Properties().group(ItemGroup.MISC).rarity(Rarity.RARE).maxStackSize(1)) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flag) {
			tooltips.add(new TranslationTextComponent("tooltip." + RespawnablePets.MODID + ".etheric_gem").applyTextStyle(TextFormatting.GRAY));
		}
	}.setRegistryName("etheric_gem");
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(etheric_gem);
	}
}

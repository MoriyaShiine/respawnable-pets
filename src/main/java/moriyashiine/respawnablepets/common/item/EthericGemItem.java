package moriyashiine.respawnablepets.common.item;

import moriyashiine.respawnablepets.common.RespawnablePets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class EthericGemItem extends Item {
	public EthericGemItem(Settings settings) {
		super(settings);
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText(RespawnablePets.MOD_ID + ".tooltip.etheric_gem").formatted(Formatting.GRAY));
	}
}

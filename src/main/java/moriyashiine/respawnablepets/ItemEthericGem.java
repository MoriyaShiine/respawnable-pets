package moriyashiine.respawnablepets;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemEthericGem extends Item {
	ItemEthericGem() {
		super(new Item.Properties().group(ItemGroup.MISC).rarity(Rarity.RARE).maxStackSize(1));
		setRegistryName("etheric_gem");
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
		if (!player.world.isRemote && !(target instanceof PlayerEntity)) {
			UUID owner = Util.getOwner(target);
			if (owner != null && owner.equals(player.getUniqueID())) {
				EntityType type = target.getType();
				String clazz = Objects.requireNonNull(type.getRegistryName()).toString();
				if (!Config.COMMON.blacklist.get().contains(clazz)) {
					if (Util.addPet(target)) player.sendStatusMessage(new TranslationTextComponent("respawnablepets.enable_respawn", target.getDisplayName()), true);
					else if (Util.removePet(target)) player.sendStatusMessage(new TranslationTextComponent("respawnablepets.disable_respawn", target.getDisplayName()), true);
				}
				else player.sendStatusMessage(new TranslationTextComponent("respawnablepets.blacklisted", target.getDisplayName()), true);
			}
			else player.sendStatusMessage(new TranslationTextComponent("respawnablepets.not_owner", target.getDisplayName()), true);
		}
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent("respawnablepets.tooltip"));
	}
}
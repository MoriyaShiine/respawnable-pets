package moriyashiine.respawnablepets;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

class ItemEthericGem extends Item {
	ItemEthericGem() {
		super();
		String name = "etheric_gem";
		setRegistryName(name);
		setTranslationKey(RespawnablePets.MODID + "." + name);
		setCreativeTab(CreativeTabs.MISC);
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (!player.world.isRemote && !(target instanceof EntityPlayer)) {
			UUID owner = Util.getOwner(target);
			if (owner != null && owner.equals(player.getPersistentID())) {
				EntityEntry entry = EntityRegistry.getEntry(target.getClass());
				if (entry != null) {
					String clazz = Objects.requireNonNull(entry.getRegistryName()).toString();
					if (!RespawnablePets.config.blacklist.contains(clazz)) {
						if (Util.addPet(target)) player.sendStatusMessage(new TextComponentTranslation("respawnablepets.enable_respawn", target.getDisplayName()), true);
						else if (Util.removePet(target)) player.sendStatusMessage(new TextComponentTranslation("respawnablepets.disable_respawn", target.getDisplayName()), true);
					}
					else player.sendStatusMessage(new TextComponentTranslation("respawnablepets.blacklisted", target.getDisplayName()), true);
				}
				else player.sendStatusMessage(new TextComponentTranslation("respawnablepets.null_entry", target.getDisplayName()), true);
			}
			else player.sendStatusMessage(new TextComponentTranslation("respawnablepets.not_owner", target.getDisplayName()), true);
		}
		return true;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(I18n.format("respawnablepets.tooltip"));
	}
}
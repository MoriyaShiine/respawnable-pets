package moriyashiine.respawnablepets;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
@Mod(modid = RespawnablePets.MODID, name = RespawnablePets.NAME, version = RespawnablePets.VERSION)
public class RespawnablePets {
	public static final String MODID = "respawnablepets", NAME = "Respawnable Pets", VERSION = "1.0.4.2";
	
	@SidedProxy(serverSide = "moriyashiine.respawnablepets.ServerProxy", clientSide = "moriyashiine.respawnablepets.ClientProxy")
	public static ServerProxy proxy;
	
	public static ModConfig config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new ModConfig(event.getSuggestedConfigurationFile());
		Item item = new Item() {
			@Override
			public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
				if (!player.world.isRemote) {
					if (target.serializeNBT().getString("OwnerUUID").equals(player.getPersistentID().toString())) {
						String name = EntityRegistry.getEntry(target.getClass()).getRegistryName().toString();
						if (Arrays.asList(config.blacklist).contains(name)) player.sendStatusMessage(new TextComponentTranslation("pet_blacklist", name), true);
						else {
							ExtendedWorld ext = ExtendedWorld.get(player.world);
							if (!ext.containsEntity(target)) {
								ext.addEntity(target);
								player.sendStatusMessage(new TextComponentTranslation("pet_added", target.getDisplayName()), true);
							}
							else {
								for (int i = ext.PETS.size() - 1; i >= 0; i--) {
									if (ext.PETS.get(i).getString("uuid").equals(target.getUniqueID().toString())) {
										ext.PETS.remove(i);
										ext.markDirty();
									}
								}
								player.sendStatusMessage(new TextComponentTranslation("pet_removed", target.getDisplayName()), true);
							}
						}
					}
					else player.sendStatusMessage(new TextComponentTranslation("pet_fail", target.getDisplayName()), true);
				}
				return true;
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
				tooltip.add(I18n.format("tooltip.respawnablepets.etheric_gem"));
			}
		}.setRegistryName(new ResourceLocation(MODID, "etheric_gem")).setTranslationKey(MODID + ".etheric_gem").setCreativeTab(CreativeTabs.MISC);
		ForgeRegistries.ITEMS.register(item);
		proxy.registerTexture(item);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}
}
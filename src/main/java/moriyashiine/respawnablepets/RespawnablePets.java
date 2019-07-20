package moriyashiine.respawnablepets;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
@Mod("respawnablepets")
public class RespawnablePets {
	public RespawnablePets() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}
	
	@SuppressWarnings("ConstantConditions")
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {
			event.getRegistry().register(new Item(new Item.Properties().group(ItemGroup.MISC).rarity(Rarity.RARE).maxStackSize(1)) {
				@Override
				public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
					if (!player.world.isRemote) {
						if (target.serializeNBT().getString("OwnerUUID").equals(player.getUniqueID().toString())) {
							ResourceLocation loc = ForgeRegistries.ENTITIES.getKey(target.getType());
							if (loc == null) player.sendStatusMessage(new TranslationTextComponent("no_entry"), true);
							else {
								String name = loc.toString();
								if (Config.COMMON.blacklist.get().contains(name)) player.sendStatusMessage(new TranslationTextComponent("pet_blacklist", name), true);
								else {
									ExtendedWorld ext = ExtendedWorld.get(player.world);
									if (!ext.containsEntity(target)) {
										CompoundNBT tag = new CompoundNBT();
										tag.put("entity", target.serializeNBT());
										tag.putString("class", target.getType().getRegistryName().toString());
										tag.putString("uuid", target.getUniqueID().toString());
										ext.PETS.add(tag);
										ext.markDirty();
										player.sendStatusMessage(new TranslationTextComponent("pet_added", target.getDisplayName()), true);
									}
									else {
										for (int i = ext.PETS.size() - 1; i >= 0; i--) {
											if (ext.PETS.get(i).getString("uuid").equals(target.getUniqueID().toString())) {
												ext.PETS.remove(i);
												ext.markDirty();
											}
										}
										player.sendStatusMessage(new TranslationTextComponent("pet_removed", target.getDisplayName()), true);
									}
								}
							}
						}
						else player.sendStatusMessage(new TranslationTextComponent("pet_fail", target.getDisplayName()), true);
					}
					return true;
				}
				
				@Override
				@OnlyIn(Dist.CLIENT)
				public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
					tooltip.add(new TranslationTextComponent("tooltip.respawnablepets.etheric_gem"));
				}
			}.setRegistryName("etheric_gem"));
		}
	}
}

package moriyashiine.respawnablepets.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import moriyashiine.respawnablepets.common.world.RPWorldState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class RespawnablePets implements ModInitializer {
	public static final String MODID = "respawnablepets";
	
	public static RPConfig config;
	
	public static final Item ETHERIC_GEM = new Item(new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.RARE).maxCount(1)) {
		@Override
		@Environment(EnvType.CLIENT)
		public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
			tooltip.add(new TranslatableText(RespawnablePets.MODID + ".tooltip.etheric_gem").formatted(Formatting.GRAY));
		}
	};
	
	public static final SoundEvent ENTITY_GENERIC_TELEPORT = new SoundEvent(new Identifier(MODID, "entity.generic.teleport"));
	
	@Override
	public void onInitialize() {
		AutoConfig.register(RPConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(RPConfig.class).getConfig();
		Registry.register(Registry.ITEM, new Identifier(MODID, "etheric_gem"), ETHERIC_GEM);
		Registry.register(Registry.SOUND_EVENT, new Identifier(MODID, "entity.generic.teleport"), ENTITY_GENERIC_TELEPORT);
	}
	
	@SuppressWarnings("ConstantConditions")
	public static PlayerEntity findOwner(World world, UUID uuid) {
		for (ServerWorld serverWorld : world.getServer().getWorlds()) {
			PlayerEntity player = serverWorld.getPlayerByUuid(uuid);
			if (player != null) {
				return player;
			}
		}
		return null;
	}
	
	public static boolean isPetRespawnable(RPWorldState worldState, Entity entity) {
		for (UUID uuid : worldState.petsToRespawn) {
			if (entity.getUuid().equals(uuid)) {
				return true;
			}
		}
		return false;
	}
}

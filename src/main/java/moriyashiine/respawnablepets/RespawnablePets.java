package moriyashiine.respawnablepets;

import moriyashiine.respawnablepets.common.handler.PetHandler;
import moriyashiine.respawnablepets.common.network.SmokePuffMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(RespawnablePets.MODID)
public class RespawnablePets {
	public static final String MODID = "respawnablepets";
	
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	
	public RespawnablePets() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}
	
	private void setup(FMLCommonSetupEvent event) {
		NETWORK_CHANNEL.registerMessage(0, SmokePuffMessage.class, SmokePuffMessage::encode, SmokePuffMessage::decode, SmokePuffMessage::handle);
		MinecraftForge.EVENT_BUS.register(new PetHandler());
	}
}

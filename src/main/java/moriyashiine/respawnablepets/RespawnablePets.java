package moriyashiine.respawnablepets;

import moriyashiine.respawnablepets.handler.PetHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** File created by mason on 4/9/20 **/
@Mod(RespawnablePets.MODID)
public class RespawnablePets {
	public static final String MODID = "respawnablepets";
	
	public RespawnablePets() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new PetHandler());
	}
}
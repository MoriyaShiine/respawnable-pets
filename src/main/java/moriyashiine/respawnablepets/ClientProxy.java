package moriyashiine.respawnablepets;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Objects;

public class ClientProxy extends ServerProxy {
	@Override
	public void registerTexture() {
		ModelLoader.setCustomModelResourceLocation(RespawnablePets.Registry.etheric_gem, 0, new ModelResourceLocation(Objects.requireNonNull(RespawnablePets.Registry.etheric_gem.getRegistryName()), "normal"));
	}
}
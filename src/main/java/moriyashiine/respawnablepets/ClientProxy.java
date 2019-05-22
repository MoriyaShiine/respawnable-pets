package moriyashiine.respawnablepets;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

@SuppressWarnings({"ConstantConditions", "unused"})
public class ClientProxy extends CommonProxy {
	@Override
	public void registerTexture(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "normal"));
	}
}
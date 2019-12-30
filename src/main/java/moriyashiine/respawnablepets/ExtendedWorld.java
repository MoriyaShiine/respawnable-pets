package moriyashiine.respawnablepets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExtendedWorld extends WorldSavedData {
	static final String TAG = RespawnablePets.MODID + ".world_data";
	
	final List<NBTTagCompound> pets = new ArrayList<>();
	
	public ExtendedWorld(String name) {
		super(name);
	}
	
	@Override
	@Nonnull
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (NBTTagCompound pet : pets) list.appendTag(pet);
		nbt.setTag("pets", list);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("pets", NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) pets.add(list.getCompoundTagAt(i));
	}
	
	static ExtendedWorld get() {
		World world = DimensionManager.getWorld(0);
		ExtendedWorld data = (ExtendedWorld) Objects.requireNonNull(world.getMapStorage()).getOrLoadData(ExtendedWorld.class, TAG);
		if (data == null) {
			data = new ExtendedWorld(TAG);
			world.getMapStorage().setData(TAG, data);
		}
		return data;
	}
	
	boolean containsEntity(EntityLivingBase entity) {
		for (NBTTagCompound nbt : pets) if (entity.getPersistentID().equals(nbt.getUniqueId("UUID"))) return true;
		return false;
	}
}
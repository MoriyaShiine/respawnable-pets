package moriyashiine.respawnablepets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ConstantConditions", "WeakerAccess", "NullableProblems"})
public class ExtendedWorld extends WorldSavedData {
	public static final String TAG = RespawnablePets.MODID + ".world_data";
	
	public final List<NBTTagCompound> PETS = new ArrayList<>();
	
	public ExtendedWorld(String name) {
		super(name);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (NBTTagCompound pet : PETS) list.appendTag(pet);
		nbt.setTag("pets", list);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("pets", NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) PETS.add(list.getCompoundTagAt(i));
	}
	
	public static ExtendedWorld get(World world) {
		ExtendedWorld data = (ExtendedWorld) world.getMapStorage().getOrLoadData(ExtendedWorld.class, TAG);
		if (data == null) {
			data = new ExtendedWorld(TAG);
			world.getMapStorage().setData(TAG, data);
		}
		return data;
	}
	
	public void addEntity(EntityLivingBase entity) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("entity", entity.serializeNBT());
		tag.setString("class", EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
		tag.setString("uuid", entity.getPersistentID().toString());
		PETS.add(tag);
		markDirty();
	}
	
	public boolean containsEntity(EntityLivingBase entity) {
		for (NBTTagCompound nbt : PETS) if (nbt.getString("uuid").equals(entity.getPersistentID().toString())) return true;
		return false;
	}
	
	public void trySpawn(World world, EntityPlayer player) {
		for (int i = PETS.size() - 1; i >= 0; i--) {
			NBTTagCompound tag = PETS.get(i);
			NBTTagCompound entityTag = tag.getCompoundTag("entity");
			if (entityTag.getString("OwnerUUID").equals(player.getPersistentID().toString())) {
				EntityLivingBase entity = (EntityLivingBase) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("class"))).newInstance(world);
				entity.deserializeNBT(entityTag);
				entity.setPositionAndRotation(player.posX, player.posY, player.posZ, world.rand.nextInt(360), 0);
				entity.extinguish();
				entity.clearActivePotions();
				if (world.spawnEntity(entity)) entity.heal(Float.MAX_VALUE);
			}
		}
	}
}
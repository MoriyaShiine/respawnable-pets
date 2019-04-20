package moriyashiine.respawnablepets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ExtendedWorld extends WorldSavedData
{
	public static final String TAG = RespawnablePets.MODID + ".world_data";
	
	private static final List<NBTTagCompound> PETS = new ArrayList<>();
	
	public ExtendedWorld(String name)
	{
		super(name);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < PETS.size(); i++) list.appendTag(PETS.get(i));
		nbt.setTag("pets", list);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList list = nbt.getTagList("pets", NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) PETS.set(i, list.getCompoundTagAt(i));
	}
	
	public static ExtendedWorld get(World world)
	{
		ExtendedWorld data = (ExtendedWorld) world.getMapStorage().getOrLoadData(ExtendedWorld.class, TAG);
		if (data == null)
		{
			data = new ExtendedWorld(TAG);
			world.getMapStorage().setData(TAG, data);
		}
		return data;
	}
	
	public void addEntity(EntityTameable entity)
	{
		if (!Arrays.asList(RespawnablePets.config.name_blacklist).contains(entity.getCustomNameTag()))
		{
			String name = EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString();
			if (!Arrays.asList(RespawnablePets.config.blacklist).contains(name))
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("entity", entity.serializeNBT());
				tag.setString("class", name);
				tag.setString("owner", entity.getOwnerId().toString());
				PETS.add(tag);
				markDirty();
			}
		}
	}
	
	public void trySpawn(World world, EntityPlayer player)
	{
		for (int i = PETS.size() - 1; i >= 0; i--)
		{
			NBTTagCompound tag = PETS.get(i);
			if (tag.getString("owner").equals(player.getUniqueID().toString()))
			{
				EntityTameable entity = (EntityTameable) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("class"))).newInstance(world);
				entity.deserializeNBT(tag.getCompoundTag("entity"));
				entity.setPositionAndRotation(player.posX, player.posY, player.posZ, world.rand.nextInt(360), 0);
				entity.setHealth(entity.getMaxHealth());
				if (world.spawnEntity(entity))
				{
					PETS.remove(i);
					markDirty();
				}
			}
		}
	}
}
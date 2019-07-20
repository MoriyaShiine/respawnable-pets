package moriyashiine.respawnablepets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class ExtendedWorld extends WorldSavedData {
	private static final String TAG = "respawnablepets.world_data";
	
	final List<CompoundNBT> PETS = new ArrayList<>();
	
	private ExtendedWorld(String name) {
		super(name);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		list.addAll(PETS);
		compound.put("pets", list);
		return compound;
	}
	
	@Override
	public void read(CompoundNBT nbt) {
		ListNBT list = nbt.getList("pets", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) PETS.add(list.getCompound(i));
	}
	
	static ExtendedWorld get(World world) {
		return world.getServer().getWorld(DimensionType.field_223227_a_).getSavedData().getOrCreate(() -> new ExtendedWorld(TAG), TAG);
	}
	
	boolean containsEntity(LivingEntity entity) {
		for (CompoundNBT nbt : PETS) if (nbt.getString("uuid").equals(entity.getUniqueID().toString())) return true;
		return false;
	}
	
	void trySpawn(World world, PlayerEntity player) {
		for (int i = PETS.size() - 1; i >= 0; i--) {
			CompoundNBT tag = PETS.get(i);
			CompoundNBT entityTag = tag.getCompound("entity");
			if (entityTag.getString("OwnerUUID").equals(player.getUniqueID().toString())) {
				LivingEntity entity = (LivingEntity) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("class"))).create(world);
				entity.deserializeNBT(entityTag);
				entity.setPositionAndRotation(player.posX, player.posY, player.posZ, world.rand.nextInt(360), 0);
				entity.extinguish();
				entity.clearActivePotions();
				if (world.addEntity(entity)) entity.heal(Float.MAX_VALUE);
			}
		}
	}
}
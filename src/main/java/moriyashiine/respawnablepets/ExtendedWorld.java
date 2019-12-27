package moriyashiine.respawnablepets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExtendedWorld extends WorldSavedData {
	private static final String TAG = RespawnablePets.MODID + ".world_data";
	
	final List<CompoundNBT> pets = new ArrayList<>();
	
	private ExtendedWorld(String name) {
		super(name);
	}
	
	@Override
	@Nonnull
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		list.addAll(pets);
		compound.put("pets", list);
		return compound;
	}
	
	@Override
	public void read(CompoundNBT nbt) {
		ListNBT list = nbt.getList("pets", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) pets.add(list.getCompound(i));
	}
	
	static ExtendedWorld get(World world) {
		return Objects.requireNonNull(world.getServer()).getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(() -> new ExtendedWorld(TAG), TAG);
	}
	
	boolean containsEntity(LivingEntity entity) {
		for (CompoundNBT nbt : pets) if (nbt.getUniqueId("UUID").equals(entity.getUniqueID())) return true;
		return false;
	}
}
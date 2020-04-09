package moriyashiine.respawnablepets.world;

import moriyashiine.respawnablepets.RespawnablePets;
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
import java.util.UUID;

/** File created by mason on 4/9/20 **/
public class RPWorld extends WorldSavedData {
	private static final String NAME = RespawnablePets.MODID + ".world_data";
	
	public final List<CompoundNBT> storedPets = new ArrayList<>();
	public final List<UUID> petsToRespawn = new ArrayList<>();
	
	public RPWorld(String name) {
		super(name);
	}
	
	@Override
	public void read(@Nonnull CompoundNBT nbt) {
		ListNBT storedPets = nbt.getList("storedPets", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < storedPets.size(); i++) {
			this.storedPets.add(storedPets.getCompound(i));
		}
		ListNBT petsToRespawn = nbt.getList("petsToRespawn", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < petsToRespawn.size(); i++) {
			this.petsToRespawn.add(petsToRespawn.getCompound(i).getUniqueId("uuid"));
		}
	}
	
	@Override
	@Nonnull
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		ListNBT storedPets = new ListNBT();
		storedPets.addAll(this.storedPets);
		nbt.put("storedPets", storedPets);
		ListNBT petsToRespawn = new ListNBT();
		for (UUID uuid : this.petsToRespawn) {
			CompoundNBT tag = new CompoundNBT();
			tag.putUniqueId("uuid", uuid);
			petsToRespawn.add(tag);
		}
		nbt.put("petsToRespawn", petsToRespawn);
		return nbt;
	}
	
	public static RPWorld get(World world) {
		return Objects.requireNonNull(world.getServer()).getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(() -> new RPWorld(NAME), NAME);
	}
}
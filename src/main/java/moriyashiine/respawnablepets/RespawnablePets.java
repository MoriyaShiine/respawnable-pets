package moriyashiine.respawnablepets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = RespawnablePets.MODID, name = RespawnablePets.NAME, version = RespawnablePets.VERSION)
public class RespawnablePets
{
	public static final String MODID = "respawnablepets", NAME = "Respawnable Pets", VERSION = "1.0";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CapabilityManager.INSTANCE.register(ExtendedPlayer.class, new ExtendedPlayer(), ExtendedPlayer::new);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}
	
	private static class ModEventHandler
	{
		private static final ResourceLocation CAP = new ResourceLocation(MODID, "cap");
		
		@SubscribeEvent
		public void attachCapabilityE(AttachCapabilitiesEvent<Entity> event)
		{
			if (event.getObject() instanceof EntityPlayer) event.addCapability(CAP, new ExtendedPlayer());
		}
		
		@SubscribeEvent
		public void clonePlayer(PlayerEvent.Clone event)
		{
			event.getEntityPlayer().getCapability(ExtendedPlayer.CAPABILITY, null).deserializeNBT(event.getOriginal().getCapability(ExtendedPlayer.CAPABILITY, null).serializeNBT());
		}
		
		@SubscribeEvent
		public void onWakeUp(PlayerWakeUpEvent event)
		{
			EntityPlayer player = event.getEntityPlayer();
			World world = player.world;
			if (!world.isRemote)
			{
				BlockPos bedPos = player.getBedLocation();
				if (bedPos != null)
				{
					ExtendedPlayer cap = player.getCapability(ExtendedPlayer.CAPABILITY, null);
					for (int i = 0; i < cap.PET_TYPES.size(); i++)
					{
						EntityTameable entity = (EntityTameable) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(cap.PET_TYPES.get(i))).newInstance(world);
						entity.deserializeNBT(cap.PET_TAGS.get(i));
						entity.setPositionAndRotation(bedPos.getX(), bedPos.getY(), bedPos.getZ(), world.rand.nextInt(360), 0);
						entity.setHealth(entity.getMaxHealth());
						world.spawnEntity(entity);
					}
					cap.PET_TYPES.clear();
					cap.PET_TAGS.clear();
				}
			}
		}
		
		@SubscribeEvent
		public void onLivingDeath(LivingDeathEvent event)
		{
			if (event.getEntityLiving() instanceof EntityTameable)
			{
				EntityTameable entity = (EntityTameable) event.getEntityLiving();
				if (entity.getOwner() != null)
				{
					ExtendedPlayer cap = entity.getOwner().getCapability(ExtendedPlayer.CAPABILITY, null);
					cap.PET_TYPES.add(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
					cap.PET_TAGS.add(entity.serializeNBT());
				}
			}
		}
	}
	
	private static class ExtendedPlayer implements ICapabilitySerializable<NBTTagCompound>, IStorage<ExtendedPlayer>
	{
		@CapabilityInject(ExtendedPlayer.class)
		public static final Capability<ExtendedPlayer> CAPABILITY = null;
		
		public final List<String> PET_TYPES = new ArrayList<>();
		public final List<NBTTagCompound> PET_TAGS = new ArrayList<>();
		
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			return getCapability(capability, facing) != null;
		}
		
		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			return capability == CAPABILITY ? CAPABILITY.cast(this) : null;
		}
		
		@Override
		public NBTTagCompound serializeNBT()
		{
			return (NBTTagCompound) CAPABILITY.getStorage().writeNBT(CAPABILITY, this, null);
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			CAPABILITY.getStorage().readNBT(CAPABILITY, this, null, nbt);
		}
		
		@Override
		public NBTBase writeNBT(Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing side)
		{
			NBTTagCompound tag = new NBTTagCompound();
			for (int i = 0; i < PET_TYPES.size(); i++)
			{
				tag.setString("petType" + i, PET_TYPES.get(i));
				tag.setTag("petTag" + i, PET_TAGS.get(i));
			}
			return tag;
		}
		
		@Override
		public void readNBT(Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing side, NBTBase nbt)
		{
			NBTTagCompound tag = (NBTTagCompound) nbt;
			for (int i = 0; i < tag.getSize(); i++)
			{
				PET_TYPES.set(i, tag.getString("petType" + i));
				PET_TAGS.set(i, (NBTTagCompound) tag.getTag("petTag" + i));
			}
		}
	}
}
package moriyashiine.respawnablepets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModEventHandler
{
	@SubscribeEvent
	public void onWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (!world.isRemote) ExtendedWorld.get(world).trySpawn(world, player);
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
		EntityLivingBase living = event.getEntityLiving();
		World world = living.world;
		if (!world.isRemote && living instanceof IEntityOwnable)
		{
			if (((IEntityOwnable) living).getOwnerId() != null)
			{
				event.setCanceled(true);
				ExtendedWorld.get(world).addEntity(living);
				living.setDead();
			}
		}
	}
}
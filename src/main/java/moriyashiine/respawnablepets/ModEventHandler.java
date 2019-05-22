package moriyashiine.respawnablepets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@SuppressWarnings("unused")
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
	public void onLivingDamage(LivingDamageEvent event)
	{
		EntityLivingBase living = event.getEntityLiving();
		World world = living.world;
		if (!world.isRemote)
		{
			if (living.getHealth() - event.getAmount() <= 0 && living.serializeNBT().hasKey("OwnerUUID"))
			{
				event.setCanceled(true);
				ExtendedWorld.get(world).addEntity(living);
				living.setDead();
				if (world.getGameRules().getBoolean("showDeathMessages") && event.getSource().getTrueSource() instanceof EntityLivingBase)
				{
					EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(living.serializeNBT().getString("OwnerUUID")));
					if (player != null) player.sendMessage(living.getCombatTracker().getDeathMessage());
				}
			}
		}
	}
}
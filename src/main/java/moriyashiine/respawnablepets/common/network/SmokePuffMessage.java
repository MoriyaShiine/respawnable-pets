package moriyashiine.respawnablepets.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SmokePuffMessage {
	private final int entityId;
	
	public SmokePuffMessage(int entityId) {
		this.entityId = entityId;
	}
	
	public static void encode(SmokePuffMessage message, PacketBuffer buffer) {
		buffer.writeInt(message.entityId);
	}
	
	public static SmokePuffMessage decode(PacketBuffer buffer) {
		return new SmokePuffMessage(buffer.readInt());
	}
	
	@SuppressWarnings("Convert2Lambda")
	public static void handle(SmokePuffMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		if (context.getDirection().getReceptionSide().isClient())
		{
			//big dumb
			context.enqueueWork(new Runnable() {
				@Override
				public void run() {
					World world = Minecraft.getInstance().world;
					if (world != null) {
						Entity entity = world.getEntityByID(message.entityId);
						if (entity != null) {
							for (int i = 0; i < 64; i++) {
								world.addParticle(ParticleTypes.SMOKE, entity.getPosXRandom(1), entity.getPosYRandom(), entity.getPosZRandom(1), 0, 0, 0);
							}
						}
					}
				}
			});
		}
		context.setPacketHandled(true);
	}
}
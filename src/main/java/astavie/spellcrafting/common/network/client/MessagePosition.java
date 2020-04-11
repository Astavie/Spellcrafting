package astavie.spellcrafting.common.network.client;

import astavie.spellcrafting.client.util.ClientUtils;
import astavie.spellcrafting.common.network.IMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessagePosition implements IMessage {

	private int entity;
	private Vec3d pos;
	private Vec2f rot;

	public MessagePosition() {
	}

	public MessagePosition(Entity entity, Vec3d pos, Vec2f rot) {
		this.entity = entity.getEntityId();
		this.pos = pos;
		this.rot = rot;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(entity);
		buffer.writeDouble(pos.x);
		buffer.writeDouble(pos.y);
		buffer.writeDouble(pos.z);
		buffer.writeFloat(rot.x);
		buffer.writeFloat(rot.y);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		this.entity = buffer.readInt();
		this.pos = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		this.rot = new Vec2f(buffer.readFloat(), buffer.readFloat());
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		context.enqueueWork(() -> {
			Entity entity = ClientUtils.getClientWorld().getEntityByID(this.entity);
			if (entity != null) {
				entity.forceSetPosition(pos.x, pos.y, pos.z);
				entity.setPositionAndRotation(pos.x, pos.y, pos.z, rot.y, rot.x);
				if (entity instanceof LivingEntity) {
					((LivingEntity) entity).rotationYawHead = rot.y;
					((LivingEntity) entity).prevRotationYawHead = rot.y;
					((LivingEntity) entity).renderYawOffset = rot.y;
					((LivingEntity) entity).prevRenderYawOffset = rot.y;
				}
			}
		});
		context.setPacketHandled(true);
	}

}

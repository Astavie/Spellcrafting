package astavie.spellcrafting.common.network.server;

import astavie.spellcrafting.api.item.ISpellItem;
import astavie.spellcrafting.api.util.Location;
import astavie.spellcrafting.client.util.ClientUtils;
import astavie.spellcrafting.common.caster.CasterCapability;
import astavie.spellcrafting.common.caster.CasterPlayer;
import astavie.spellcrafting.common.network.IMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageCast implements IMessage {

	private int entity;
	private Location location;

	private Hand hand;

	public MessageCast() {
	}

	public static MessageCast createMessage(PlayerEntity player, Hand hand, double reach) {
		RayTraceResult result = ClientUtils.rayTrace(player, Animation.getPartialTickTime(), reach);

		MessageCast message = new MessageCast();
		message.hand = hand;

		if (result.getType() == RayTraceResult.Type.ENTITY) {
			message.entity = ((EntityRayTraceResult) result).getEntity().getEntityId();
		} else {
			message.location = new Location(player.dimension, result.getHitVec(), ((BlockRayTraceResult) result).getPos());
		}

		return message;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBoolean(hand == Hand.MAIN_HAND);

		buffer.writeBoolean(location == null);
		if (location == null) {
			// Entity
			buffer.writeInt(entity);
		} else {
			// Block
			buffer.writeInt(location.getDimension().getId());
			buffer.writeDouble(location.getPos().x);
			buffer.writeDouble(location.getPos().y);
			buffer.writeDouble(location.getPos().z);
			buffer.writeBlockPos(location.getBlock());
		}
	}

	@Override
	public void decode(PacketBuffer buffer) {
		hand = buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;

		if (buffer.readBoolean()) {
			// Entity
			entity = buffer.readInt();
		} else {
			// Block
			DimensionType dimension = DimensionType.getById(buffer.readInt());
			Vec3d vec = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
			BlockPos pos = buffer.readBlockPos();

			location = new Location(dimension, vec, pos);
		}
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		context.enqueueWork(() -> {
			PlayerEntity player = context.getSender();
			if (player == null)
				return;

			ItemStack stack = player.getHeldItem(hand);
			if (!(stack.getItem() instanceof ISpellItem) || !((ISpellItem) stack.getItem()).canCastSpell(player, hand, stack))
				return;

			// Cast spell
			player.func_226292_a_(Hand.MAIN_HAND, true);

			CasterPlayer caster = (CasterPlayer) player.getCapability(CasterCapability.CASTER_CAPABILITY).orElseThrow(IllegalStateException::new);
			if (location == null) {
				caster.tEntity = player.world.getEntityByID(entity);
				caster.tLocation = null;
			} else {
				caster.tEntity = null;
				caster.tLocation = location;
			}

			((ISpellItem) stack.getItem()).castSpell(caster, stack);
		});
		context.setPacketHandled(true);
	}

}

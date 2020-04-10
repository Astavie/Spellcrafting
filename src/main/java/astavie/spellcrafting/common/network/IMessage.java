package astavie.spellcrafting.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IMessage {

	void encode(PacketBuffer buffer);

	void decode(PacketBuffer buffer);

	void handle(NetworkEvent.Context context);

}

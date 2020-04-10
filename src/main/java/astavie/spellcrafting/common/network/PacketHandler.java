package astavie.spellcrafting.common.network;

import astavie.spellcrafting.Spellcrafting;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	private static SimpleChannel INSTANCE;

	private static int id = 0;

	public static void register() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Spellcrafting.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

		registerPacket(MessageCast.class, MessageCast::new);
	}

	private static <MSG extends IMessage> void registerPacket(Class<MSG> clazz, Supplier<MSG> supplier) {
		INSTANCE.registerMessage(id++, clazz, IMessage::encode, buffer -> {
			MSG message = supplier.get();
			message.decode(buffer);
			return message;
		}, (msg, s) -> msg.handle(s.get()));
	}

	public static void sendToPlayer(ServerPlayerEntity player, IMessage message) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static void sendToPlayers(PacketDistributor.PacketTarget target, IMessage message) {
		INSTANCE.send(target, message);
	}

	public static void sendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}

}

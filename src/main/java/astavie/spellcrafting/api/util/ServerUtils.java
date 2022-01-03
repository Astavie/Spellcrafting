package astavie.spellcrafting.api.util;

import net.minecraft.server.MinecraftServer;

public final class ServerUtils {

    private ServerUtils() {
    }
    
    public static MinecraftServer server;

    public static long getTime() {
        return server.getOverworld().getTime();
    }

}

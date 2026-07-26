package io.mikaple.utils;

import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundInteractPacket;

import static io.mikaple.Main.session;

public class GameUtils {
    public static void interact(int entityId, Vector3d location,boolean isSneaking) {
        session.send(new ServerboundInteractPacket(entityId,location,isSneaking));
    }

    public static void interactPlayer(String name) {
    }
}

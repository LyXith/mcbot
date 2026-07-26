package io.mikaple.utils;

import io.mikaple.data.EntityData;
import org.cloudburstmc.math.vector.Vector3d;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundInteractPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemOnPacket;

import java.util.List;

import static io.mikaple.Main.profileMap;

public class GameUtils {
    public static void interact(int entityId, Vector3d location, boolean isSneaking, ClientSession session) {
        session.send(new ServerboundInteractPacket(entityId,location,isSneaking));
    }

    public static void interactPlayer(String name,ClientSession session) {
        List<EntityData> players = EntityData.getPlayers();
        for (EntityData player : players) {
            GameProfile profile = profileMap.get(player.entity.getUuid());
            if (profile != null && profile.getName().equals(name)) {
                Vector3d location = Vector3d.from(
                        0,
                        0,
                        0
                );
                interact(player.entity.getEntityId(),location,false,session);
                break;
            }
        }
    }

    public static void use(Vector3i position,Vector3i blockPos,ClientSession session) {
        session.send(new ServerboundUseItemOnPacket(
                position,
                Direction.UP,
                Hand.MAIN_HAND,
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ(),
                false,
                false,
                0
        ));
    }
}

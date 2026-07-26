package io.mikaple.utils;

import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;

import java.time.Instant;
import java.util.BitSet;

import static io.mikaple.Main.session;

public class ChatUtils {

    public static void sendChat(String string,ClientSession session) {
        session.send(new ServerboundChatPacket(
                string,
                Instant.now().toEpochMilli(),
                0L,
                null,
                0,
                new BitSet(),
                0
        ));
    }

    public static void sendCommand(String command,ClientSession session) {
        session.send(new ServerboundChatCommandPacket(command));
    }

    public static void sendMsg(String msg,String player,ClientSession session) {
        sendCommand("msg " + player + " " + msg,session);
    }

    public static void sendChat(String string) {
        sendChat(string,session);
    }

    public static void sendCommand(String command) {
        sendCommand(command,session);
    }

    public static void sendMsg(String msg,String player) {
        sendMsg(msg,player,session);
    }
}

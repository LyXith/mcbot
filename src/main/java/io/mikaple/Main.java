package io.mikaple;

import io.mikaple.command.Command;
import io.mikaple.command.CommandManager;
import io.mikaple.utils.ChatUtils;
import io.mikaple.utils.ClientBuilder;
import io.mikaple.utils.Utils;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.ConnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ParticleStatus;
import org.geysermc.mcprotocollib.protocol.data.game.setting.SkinPart;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import static io.mikaple.commands.CmdCommand.cmdCommand;
import static io.mikaple.commands.HelpCommand.*;
import static io.mikaple.commands.MidiCommand.midiCommand;
import static io.mikaple.commands.PingServerCommand.pingCommand;
import static io.mikaple.commands.RespawnCommand.respawnCommand;
import static io.mikaple.commands.SendCommand.sendCommand;
import static io.mikaple.commands.WhoamiCommand.whoamiCommand;


public class Main {
    public static final boolean debug = false;
    public static ClientSession session;
    public static Logger log = LoggerFactory.getLogger("bot");
    public static File logFolder = new File("logs");
    public static boolean reconnect = true;
    public static boolean registered = false;
    static void main() {
        if (!logFolder.exists()) {
            logFolder.mkdir();
        }
        while (true) {
            createAndRun("zakoloop","zakocraft143");
            Object lock = new Object();
            synchronized (lock) {
                try {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("Bot stopped");
        }
    }

    public static void createAndRun(String name,String passwd) {
        CountDownLatch latch = new CountDownLatch(1);
        ClientSession client = ClientNetworkSessionFactory.factory()
                .setAddress("mc.weeaxe.cn")
                .setProtocol(new MinecraftProtocol(name))
                .create();
        ClientBuilder.buildClient(client,passwd,latch);
        client.connect();
        registerCommand();
        try {
            latch.await();  // 等待直到断开连接
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void registerCommand() {
        if (registered) return;
        CommandManager.register(helpCommand);
        CommandManager.register(sendCommand);
        CommandManager.register(cmdCommand);
        CommandManager.register(respawnCommand);
        CommandManager.register(midiCommand);
        CommandManager.register(whoamiCommand);
        CommandManager.register(pingCommand);
        registered = true;
    }
}

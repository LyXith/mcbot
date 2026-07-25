package io.mikaple;

import io.mikaple.command.CommandManager;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static io.mikaple.commands.HelpCommand.*;
import static io.mikaple.commands.SendCommand.sendCommand;


public class Main {
    public static final boolean debug = false;
    public static ClientSession session;
    public static Logger log = LoggerFactory.getLogger("bot");
    static void main() {
        CountDownLatch latch = new CountDownLatch(1);

        ClientSession client = ClientNetworkSessionFactory.factory()
                .setAddress("mc.weeaxe.cn")
                .setProtocol(new MinecraftProtocol("zakoloop"))
                .create();

        client.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(Session session, Packet packet) {
                if (packet instanceof ClientboundLoginPacket) {
                    session.send(new ServerboundClientInformationPacket(
                            "zh_CN",
                            2,
                            ChatVisibility.FULL,
                            true,
                            Arrays.asList(SkinPart.CAPE, SkinPart.JACKET),
                            HandPreference.RIGHT_HAND,
                            false,
                            true,
                            ParticleStatus.ALL));
                } else if (packet instanceof ClientboundSystemChatPacket chatPacket) {
                    if (debug) {
                        log.info("Received: {}", chatPacket.getContent());
                    }
                    ChatProcesser.processChat(chatPacket.getContent());
                }
            }
        });

        client.addListener(new SessionAdapter() {
            @Override
            public void connected(ConnectedEvent event) {
                session = (ClientSession) event.getSession();
                log.info("Bot connected to server");
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                log.info("Bot disconnected: {}", event.getReason());
                if (event.getCause() != null) {
                    log.error("断开原因: ", event.getCause());
                }
                latch.countDown();
            }
        });

        client.connect();
        CommandManager.init(session);
        CommandManager.register(helpCommand);
        CommandManager.register(sendCommand);

//        String path = "/home/mikaple/IdeaProjects/mcbot/realms.mid";
//        File file = new File(path);
//        log.info("文件路径: {}", file.getAbsolutePath());
//        log.info("文件是否存在: {}", file.exists());
//        log.info("文件是否可读: {}", file.canRead());
//
//        CompletableFuture.runAsync(() -> {
//            try {
//                // 等待一小段时间确保登录完成
//                Thread.sleep(5000);
//                MidiProcesser.play(file, session);
//            } catch (Exception e) {
//                log.error("MIDI播放失败: ", e);
//            }
//        });


        try {
            latch.await();  // 等待直到断开连接
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Bot stopped");
    }
}

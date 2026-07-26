package io.mikaple.utils;

import io.mikaple.ChatProcesser;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.ConnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ParticleStatus;
import org.geysermc.mcprotocollib.protocol.data.game.setting.SkinPart;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import static io.mikaple.Main.*;
import static io.mikaple.commands.BotCommand.botConnectStatus;
import static io.mikaple.commands.BotCommand.botMap;

public class ClientBuilder {
    public static void buildClient(ClientSession client,String passwd, CountDownLatch latch,boolean autoRegister, boolean main) {
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
                    if (autoRegister) {
                        ChatUtils.sendCommand("register "+passwd+" "+passwd,client);
                    }
                    ChatUtils.sendCommand("login "+passwd,client);
                } else if (packet instanceof ClientboundSystemChatPacket chatPacket) {
                    if (debug) {
                        log.info("Received: {}", chatPacket.getContent());
                    }
                    if (main) ChatProcesser.processChat(chatPacket.getContent());
                }
            }
        });

        client.addListener(new SessionAdapter() {
            @Override
            public void connected(ConnectedEvent event) {
                if (main) session = (ClientSession) event.getSession();
                log.info("Bot connected to server");
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                log.info("Bot disconnected: {}", event.getReason());
                if (event.getCause() != null) {
                    log.error("断开原因: ", event.getCause());
                }
                if (!main) {
                    botMap.remove(client.getPacketProtocol().getProfile().getName());
                    botConnectStatus.remove(client.getPacketProtocol().getProfile().getName());
                }
                latch.countDown();
            }
        });
    }
}

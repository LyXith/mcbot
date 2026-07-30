package io.mikaple.utils;

import io.mikaple.Settings;
import io.mikaple.processer.ChatProcesser;
import io.mikaple.command.CommandManager;
import io.mikaple.data.EntityData;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.ConnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ParticleStatus;
import org.geysermc.mcprotocollib.protocol.data.game.setting.SkinPart;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoRemovePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundAddEntityPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundEntityPositionSyncPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundRemoveEntitiesPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static io.mikaple.Main.*;
import static io.mikaple.Settings.debug;
import static io.mikaple.commands.AttackCommand.attackCommand;
import static io.mikaple.commands.BotCommand.*;
import static io.mikaple.commands.CmdCommand.cmdCommand;
import static io.mikaple.commands.HelpCommand.helpCommand;
import static io.mikaple.commands.InteractCommand.interactCommand;
import static io.mikaple.commands.MusicCommand.musicCommand;
import static io.mikaple.commands.PingServerCommand.pingCommand;
import static io.mikaple.commands.RespawnCommand.respawnCommand;
import static io.mikaple.commands.SendCommand.sendCommand;
import static io.mikaple.commands.SettingsCommand.settingsCommand;
import static io.mikaple.commands.WhoamiCommand.whoamiCommand;
import static io.mikaple.utils.LogUtils.log;

public class ClientBuilder {
    public static void buildClient(ClientSession client,String passwd, CountDownLatch latch,boolean autoRegister, boolean main) {
        client.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(Session session, Packet packet) {
                int selfId = 0;
                switch (packet) {
                    case ClientboundLoginPacket loginPacket -> {
                        selfId = loginPacket.getEntityId();
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
                    }

                    case ClientboundSystemChatPacket chatPacket -> {
                        if (debug) {
                            log.info("Received: {}", chatPacket.getContent());
                        }
                        if (main) ChatProcesser.processChat(chatPacket.getContent());
                    }

                    case ClientboundAddEntityPacket addEntityPacket -> {
                        entities.add(new EntityData(addEntityPacket));
                    }

                    case ClientboundEntityPositionSyncPacket entityPositionSyncPacket -> {
                        if (main) {
                            EntityData.updateEntity(entityPositionSyncPacket);
                        }
                    }

                    case ClientboundRemoveEntitiesPacket removeEntitiesPacket -> {
                        int[] entityIds = removeEntitiesPacket.getEntityIds();
                        for (int entityId : entityIds) {
                            EntityData.removeEntity(entityId);
                        }
                    }

                    case ClientboundPlayerInfoUpdatePacket playerInfoUpdatePacket -> {
                        for (PlayerListEntry entry : playerInfoUpdatePacket.getEntries()) {
                            if (entry.getProfile() != null) {
                                profileMap.put(entry.getProfile().getId(), entry.getProfile());
                            }
                        }
                    }

                    case ClientboundPlayerInfoRemovePacket playerInfoRemovePacket -> {
                        List<UUID> UUIDs = playerInfoRemovePacket.getProfileIds();
                        for (UUID uuid : UUIDs) {
                            profileMap.remove(uuid);
                        }
                    }

                    case ClientboundPlayerPositionPacket playerPositionPacket -> {
                        session.send(new ServerboundAcceptTeleportationPacket(playerPositionPacket.getId()));
                    }

                    default -> {}
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

    public static void createAndRun(String name,String passwd) {
        ClientSession client = createBot(name);
        CountDownLatch latch = runBot(client,passwd,false,true);
        registerCommand();
        try {
            latch.await();  // 等待直到断开连接
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static ClientSession createBot(String name) {
        return ClientNetworkSessionFactory.factory()
                .setAddress(Settings.serverAddress)
                .setProtocol(new MinecraftProtocol(name))
                .create();
    }

    public static CountDownLatch runBot(ClientSession client,String passwd,boolean autoRegister,boolean main) {
        CountDownLatch latch = new CountDownLatch(1);
        ClientBuilder.buildClient(client,passwd,latch,autoRegister,main);
        client.connect();
        return latch;
    }

    public static void registerCommand() {
        if (registered) return;
        CommandManager.register(helpCommand);
        CommandManager.register(sendCommand);
        CommandManager.register(cmdCommand);
        CommandManager.register(respawnCommand);
        CommandManager.register(musicCommand);
        CommandManager.register(whoamiCommand);
        CommandManager.register(pingCommand);
        CommandManager.register(botCommand);
        CommandManager.register(interactCommand);
        CommandManager.register(attackCommand);
        CommandManager.register(settingsCommand);
        registered = true;
    }
}

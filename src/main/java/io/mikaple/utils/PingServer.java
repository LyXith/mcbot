package io.mikaple.utils;

import io.mikaple.MessageProcesser;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.auth.SessionService;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.protocol.MinecraftConstants;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PingServer {

    private static final long TIMEOUT_MS = 5000; // 超时时间，防止卡死;
    public static HashMap<String, String> ping(SocketAddress address) {
        CountDownLatch latch = new CountDownLatch(1);
        HashMap<String, String> serverStatus = new HashMap<>();
        SessionService sessionService = new SessionService();
        MinecraftProtocol protocol = new MinecraftProtocol();

        ClientSession client = ClientNetworkSessionFactory.factory()
                .setRemoteSocketAddress(address)
                .setProtocol(protocol)
                .create();

        client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);

        client.setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, (_, info) -> {
            if (info.getVersionInfo() != null) {
                serverStatus.put("version", info.getVersionInfo().getVersionName());
            } else {
                serverStatus.put("version", "null");
            }

            if (info.getPlayerInfo() != null) {
                serverStatus.put("playerNumber", info.getPlayerInfo().getOnlinePlayers()
                        + " / " + info.getPlayerInfo().getMaxPlayers());
                String names = info.getPlayerInfo().getPlayers()
                        .stream()
                        .map(GameProfile::getName)
                        .collect(Collectors.joining(", "));
                serverStatus.put("playerList", names);
                serverStatus.put("motd", MessageProcesser.toPlainText(info.getDescription()));
            } else {
                serverStatus.put("playerNumber", "null");
                serverStatus.put("playerList", "null");
                serverStatus.put("motd", "null");
            }
            latch.countDown();
        });

        client.setFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY,
                (session, pingTime) -> serverStatus.put("ping", String.valueOf(pingTime)));

        // 记录断开原因，方便排查连接失败的情况
        client.addListener(new SessionAdapter() {
            @Override
            public void disconnected(DisconnectedEvent event) {
                if (!serverStatus.containsKey("version")) {
                    serverStatus.put("error", String.valueOf(event.getReason()));
                }
            }
        });

        client.connect();

        long start = System.currentTimeMillis();
        try {
            // 等待最多 5 秒
            latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (client.isConnected()) {
            client.disconnect("Timeout"); // 超时兜底，防止连接一直挂着
        }

        return serverStatus;
    }
}
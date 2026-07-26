package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.command.arguments.Argument;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;
import io.mikaple.utils.PingServer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PingServerCommand {
    public static Command pingCommand = new Command()
            .literal("ping")
            .argument(new StringArgument())
            .executes((session,arguments,_) -> {
                Argument arg = arguments.getFirst();
                if (arg instanceof StringArgument strArg) {
                    SocketAddress address = parse(strArg.string);
                    CompletableFuture.runAsync(() -> {
                        HashMap<String,String> serverStatus = PingServer.ping(address);
                        ChatUtils.sendChat("version: "+serverStatus.get("version"),session);
                        ChatUtils.sendChat("player: "+serverStatus.get("playerNumber"),session);
                        ChatUtils.sendChat("player: " + serverStatus.get("playerList"),session);
                    });
                }
            });

    private static SocketAddress parse(String str) {
        int colonIdx = str.lastIndexOf(':');
        if (colonIdx == -1) {
            return new InetSocketAddress(str, 25565);
        }
        String host = str.substring(0, colonIdx);
        int port = Integer.parseInt(str.substring(colonIdx + 1));
        return new InetSocketAddress(host, port);
    }
}

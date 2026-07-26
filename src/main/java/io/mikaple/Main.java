package io.mikaple;

import io.mikaple.command.CommandManager;
import io.mikaple.utils.ClientBuilder;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import static io.mikaple.commands.BotCommand.botCommand;
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
                .setAddress("mc.weeaxe.cn")
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
        CommandManager.register(midiCommand);
        CommandManager.register(whoamiCommand);
        CommandManager.register(pingCommand);
        CommandManager.register(botCommand);
        registered = true;
    }
}

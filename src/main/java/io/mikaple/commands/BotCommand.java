package io.mikaple.commands;

import io.mikaple.Main;
import io.mikaple.command.Command;
import io.mikaple.command.arguments.Argument;
import io.mikaple.command.arguments.StringArgument;
import org.geysermc.mcprotocollib.network.ClientSession;

import java.util.HashMap;

public class BotCommand {
    private static HashMap<String, ClientSession> botMap = new HashMap<>();
    private static HashMap<String, Boolean> botConnectStatus = new HashMap<>();
    public static Command botCommand = new Command()
            .literal("bot")
            .argument(new StringArgument())
            .argument(new StringArgument())
            .executes((_,args,_) -> {
                Argument arg1 = args.getFirst();
                Argument arg2 = args.get(1);
                if (arg1 instanceof StringArgument strArg) {
                    switch (strArg.string) {
                        case "spawn":
                            if (arg2 instanceof StringArgument strArg2) spawnBot(strArg2.string);
                            break;
                        case "kill":
                            if (arg2 instanceof StringArgument strArg2) killBot(strArg2.string);
                            break;
                    }
                }
            });

    private static void spawnBot(String name) {
        if (botConnectStatus.get(name) == null || !botConnectStatus.get(name)) {
            ClientSession botSession = Main.createBot(name);
            botMap.put(name, botSession);
            Main.runBot(botSession, "bot_passwd1234",true,false);
            botConnectStatus.put(name, true);
        }
    }

    private static void killBot(String name) {
        if (botMap.get(name) != null) {
            ClientSession botSession = botMap.get(name);
            botSession.disconnect("killed");
            botMap.remove(name);
            botConnectStatus.remove(name);
        }
    }
}

package io.mikaple.command;

import io.mikaple.Main;
import org.geysermc.mcprotocollib.network.ClientSession;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static List<Command> commands = new ArrayList<>();

    public static void init(ClientSession session) {
        Command.setSession(session);
    }

    public static void register(Command command) {
        if (Main.session == null) {
            throw new IllegalStateException("Client is not initialized.");
        }
        commands.add(command);
    }

    public static List<Command> getAllCommands() {
        return commands;
    }

    public static void processCommand(String input,String player) {
        String[] commandParts = input.split(" ");
        String main = commandParts[0];
        for (Command command : commands) {
            if (command.getName().equals(main)) {
                command.execute(null);
                break;
            }
        }
    }
}

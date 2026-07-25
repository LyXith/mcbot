package io.mikaple.command;

import io.mikaple.Main;
import io.mikaple.command.arguments.Argument;
import io.mikaple.command.arguments.IntegerArgument;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;
import org.geysermc.mcprotocollib.network.ClientSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    private static List<Command> commands = new ArrayList<>();

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
        String[] commandParts = splitPreserveQuotes(input);
        String main = commandParts[0];
        for (Command command : commands) {
            if (command.getName().equals(main)) {
                for (Argument argument : command.arguments) {
                    switch (argument) {
                        case IntegerArgument intArg -> intArg.integer = 0;
                        case StringArgument strArg -> strArg.string = null;
                        default -> {}
                    }
                }

                String[] args = Arrays.stream(commandParts)
                        .skip(1)
                        .toArray(String[]::new);
                boolean parseFailed = false;
                int i = 0;
                for (String arg : args) {
                    try {
                        Argument argument = command.arguments.get(i);
                        switch (argument) {
                            case IntegerArgument intArg -> intArg.integer = Integer.parseInt(arg);
                            case StringArgument strArg -> strArg.string = arg;
                            default -> {}
                        }
                        i++;
                    } catch (IndexOutOfBoundsException ignore) {
                        ChatUtils.sendChat("wrong command");
                        parseFailed = true;
                        break;
                    }
                }
                if (!parseFailed) {
                    command.execute();
                }
                break;
            }
        }
    }

    public static String[] splitPreserveQuotes(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }

        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // 处理单引号
            if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                continue; // 不保留引号
            }

            // 处理双引号
            if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
                continue; // 不保留引号
            }

            // 处理空格分隔
            if (c == ' ' && !inSingleQuotes && !inDoubleQuotes) {
                if (currentPart.length() > 0) {
                    parts.add(currentPart.toString());
                    currentPart.setLength(0);
                }
                continue;
            }

            // 普通字符
            currentPart.append(c);
        }

        // 添加最后一部分
        if (currentPart.length() > 0) {
            parts.add(currentPart.toString());
        }

        return parts.toArray(new String[0]);
    }

}

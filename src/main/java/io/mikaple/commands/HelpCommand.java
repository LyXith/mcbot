package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.command.CommandManager;
import io.mikaple.utils.ChatUtils;

import java.util.List;

public class HelpCommand {
    public static Command helpCommand = new Command()
            .literal("help")
            .executes((_,_,_) -> {
                List<Command> commands = CommandManager.getAllCommands();
                String msg = "";
                for (Command command :commands) {
                    msg = msg.concat(command.getName() + ", ");
                }
                ChatUtils.sendChat(msg);
            });

}

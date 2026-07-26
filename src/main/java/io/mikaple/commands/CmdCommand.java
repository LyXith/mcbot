package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;

public class CmdCommand {
    public static Command cmdCommand = new Command()
            .literal("cmd")
            .argument(new StringArgument())
            .executes((session,arguments,_) -> {
                if (arguments.getFirst() instanceof StringArgument strArg) {
                    ChatUtils.sendCommand(strArg.string,session);
                }
            });
}

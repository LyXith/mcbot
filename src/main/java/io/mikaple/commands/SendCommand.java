package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;

public class SendCommand {
    public static Command sendCommand = new Command()
            .literal("send")
            .argument(new StringArgument())
            .executes((session,arguments,_) -> {
                if (arguments.getFirst() instanceof StringArgument strArg) {
                    if (strArg.string != null) ChatUtils.sendChat(strArg.string,session);
                }
            });
}

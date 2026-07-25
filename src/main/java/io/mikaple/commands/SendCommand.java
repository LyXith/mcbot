package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;

public class SendCommand {
    public static Command sendCommand = new Command()
            .literal("send")
            .argument(new StringArgument())
            .executes((_,arguments) -> {

                ChatUtils.sendChat("1");
            });
}

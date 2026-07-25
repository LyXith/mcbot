package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.utils.ChatUtils;

public class WhoamiCommand {
    public static Command whoamiCommand = new Command()
            .literal("whoami")
            .executes((_,_,context) -> {
                ChatUtils.sendChat(context.executor());
            });
}

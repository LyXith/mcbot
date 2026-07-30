package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.command.arguments.Argument;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.GameUtils;

public class AttackCommand {
    public static Command attackCommand = new Command()
            .literal("attack")
            .argument(new StringArgument())
            .executes((session,arguments,_) -> {
                Argument arg = arguments.getFirst();
                if (arg instanceof StringArgument strArg) {
                    GameUtils.attackPlayer(strArg.string,session);
                }
            });
}

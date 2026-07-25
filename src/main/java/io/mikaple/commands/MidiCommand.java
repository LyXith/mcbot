package io.mikaple.commands;

import io.mikaple.command.Command;
import io.mikaple.command.arguments.IntegerArgument;

public class MidiCommand {
    public Command MidiCommand = new Command().literal("midi")
            .argument(new IntegerArgument());
}

package io.mikaple.commands;

import io.mikaple.MidiProcesser;
import io.mikaple.command.Command;
import io.mikaple.command.arguments.Argument;
import io.mikaple.command.arguments.IntegerArgument;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;

import java.io.File;
import java.util.Arrays;

public class MidiCommand {
    public static Command midiCommand = new Command().literal("midi")
            .argument(new StringArgument())
            .argument(new StringArgument())
            .executes((session,args,_) -> {
                Argument arg1 = args.getFirst();
                if (arg1 instanceof StringArgument strArg) {
                    switch (strArg.string) {
                        case "list":
                            ChatUtils.sendChat(Arrays.toString(new File("midi").list()),session);
                            break;
                        case "play":
                            Argument arg2 = args.get(1);
                            if (arg2 instanceof StringArgument strArg2){
                                File midi = new File("midi",strArg2.string);
                                if (midi.exists()) {
                                    MidiProcesser.play(midi,session);
                                } else {
                                    ChatUtils.sendChat("Play failed.",session);
                                }
                            } else {
                                ChatUtils.sendChat("Play failed.",session);
                            }
                            break;
                        case "stop":
                            MidiProcesser.stop();
                            break;
                    }
                }
            });
}

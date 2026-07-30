package io.mikaple.commands;

import io.mikaple.Settings;
import io.mikaple.command.Command;
import io.mikaple.command.arguments.Argument;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;

import java.util.Set;

public class SettingsCommand {
    public static Command settingsCommand = new Command().literal("settings")
            .argument(new StringArgument())
            .argument(new StringArgument())
            .executes((session,args,_) -> {
                Argument arg1 = args.getFirst();
                Argument arg2 = args.get(1);
                if (arg1 instanceof StringArgument strArg) {
                    switch (strArg.string) {
                        case "trust":
                            if (arg2 instanceof StringArgument strArg2) {
                                if (!Settings.trustedPlayers.contains(strArg2.string)) {
                                    Settings.trustedPlayers.add(strArg2.string);
                                    ChatUtils.sendChat("Trusted " + strArg2.string + ".",session);
                                    break;
                                }
                                ChatUtils.sendChat(strArg2.string + " is already trusted.",session);
                            }
                            break;
                        case "remove":
                            if (arg2 instanceof StringArgument strArg2) {
                                if (Settings.trustedPlayers.contains(strArg2.string)) {
                                    Settings.trustedPlayers.remove(strArg2.string);
                                    ChatUtils.sendChat("Removed " + strArg2.string + ".",session);
                                    break;
                                }
                                ChatUtils.sendChat(strArg2.string + " is not trusted.",session);
                            }
                            break;
                        case "public":
                            if (arg2 instanceof StringArgument strArg2) {
                                if (strArg2.string.isEmpty()) Settings.isPublic = !Settings.isPublic;
                                if (strArg2.string.equals("true")) Settings.isPublic = true;
                                if (strArg2.string.equals("false")) Settings.isPublic = false;
                            }
                            break;
                        case "info":
                            ChatUtils.sendChat("debug: " + Settings.debug,session);
                            ChatUtils.sendChat("owners: " + Settings.owners,session);
                            ChatUtils.sendChat("trusted players: " + Settings.trustedPlayers,session);
                            ChatUtils.sendChat("public: "+ Settings.isPublic,session);
                    }
                }
            });
}

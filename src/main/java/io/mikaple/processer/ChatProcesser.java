package io.mikaple.processer;

import io.mikaple.Main;
import io.mikaple.Settings;
import io.mikaple.command.CommandManager;
import io.mikaple.utils.LogUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;

public class ChatProcesser {
    public static void processChat(Component input) {
        System.out.println(MessageProcesser.toAnsi(input));
        String plainString = MessageProcesser.toPlainText(input);
        LogUtils.logMessage(plainString);
        if (plainString.startsWith("[玩家]") || plainString.startsWith("[地皮]") || plainString.startsWith("[世界]")) {
            Component nameNode = input.children().getFirst().children().get(1);
            HoverEvent<?> hover = nameNode.hoverEvent();
            Component hoverComp;
            if (hover != null) {
                hoverComp = (Component) hover.value();
                String playerName = textOf(hoverComp.children().get(1));
                String chatMsg = textOf(input.children().get(1));
                if (chatMsg.startsWith("!!")) {
                    String command = chatMsg.substring(2);
                    if (Settings.isPublic || Settings.owners.contains(playerName) || Settings.trustedPlayers.contains(playerName)) {
                        CommandManager.processCommand(command, playerName, Main.session);
                    }
                }
            }
        }

    }

    private static String textOf(Component c) {
        if (c instanceof TextComponent tc && !tc.content().isEmpty()) {
            return tc.content();
        }
        for (Component child : c.children()) {
            String t = textOf(child);
            if (!t.isEmpty()) return t;
        }
        return "";
    }
}

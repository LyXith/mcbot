package io.mikaple.commands;

import io.mikaple.command.Command;
import org.geysermc.mcprotocollib.protocol.data.game.ClientCommand;

import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket;

public class RespawnCommand {
    public static Command respawnCommand = new Command()
            .literal("respawn")
            .executes((session,_) -> {
                session.send(new ServerboundClientCommandPacket(ClientCommand.PERFORM_RESPAWN));
            });
}

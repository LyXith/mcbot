package io.mikaple.command;

import io.mikaple.command.arguments.Argument;
import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.Session;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private static ClientSession session;
    private List<Argument> arguments = new ArrayList<>();
    private Task task;
    @FunctionalInterface
    public interface Task {
        void run(Session session, List<Argument> arguments);
    }

    private String name;
    public Command literal(String name) {
        this.name = name;
        return this;
    }

    public Command executes(Task task) {
        this.task = task;
        return this;
    }

    public Command argument(Argument arg) {
        arguments.add(arg);
        return this;
    }

    public static void setSession(ClientSession client) {
        session = client;
    }



    public String getName() {
        return this.name;
    }

    public void execute(List<Object> args) {
        this.task.run(session, arguments);
    }
}

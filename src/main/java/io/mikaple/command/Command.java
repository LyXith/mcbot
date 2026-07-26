package io.mikaple.command;

import io.mikaple.command.arguments.Argument;
import org.geysermc.mcprotocollib.network.ClientSession;

import java.util.ArrayList;
import java.util.List;

public class Command {
    public List<Argument> arguments = new ArrayList<>();
    private Task task;
    public CommandContext context;
    @FunctionalInterface
    public interface Task {
        void run(ClientSession session, List<Argument> arguments,CommandContext context);
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

    public String getName() {
        return this.name;
    }

    public void execute(ClientSession session) {
        this.task.run(session, arguments,context);
    }
}

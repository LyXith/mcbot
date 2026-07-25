package io.mikaple.command;

import io.mikaple.Main;
import io.mikaple.command.arguments.Argument;
import org.geysermc.mcprotocollib.network.Session;

import java.util.ArrayList;
import java.util.List;

public class Command {
    public List<Argument> arguments = new ArrayList<>();
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

    public String getName() {
        return this.name;
    }

    public void execute() {
        this.task.run(Main.session, arguments);
    }
}

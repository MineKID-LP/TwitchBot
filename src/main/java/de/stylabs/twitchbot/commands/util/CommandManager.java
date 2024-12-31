package de.stylabs.twitchbot.commands.util;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public void runCommand(String commandName, String channel, CommandSender sender, String[] args) {
        if(!commands.containsKey(commandName)) return;
        commands.get(commandName).execute(channel, sender, args);
    }

    public void registerCommand(String commandName, Command command) {
        commands.put(commandName, command);
        command.onRegister();
    }

    public void unregisterCommand(String commandName) {
        if(!commands.containsKey(commandName)) return;
        commands.get(commandName).onUnregister();
        commands.remove(commandName);
    }

    public Command getCommand(String commandName) {
        return commands.get(commandName);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}

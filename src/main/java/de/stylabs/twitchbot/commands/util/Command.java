package de.stylabs.twitchbot.commands.util;

public abstract class Command {
    private final String name;
    private final String description;
    private final String usage;

    public Command(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    public void onRegister() {}
    public void onUnregister() {}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public abstract void execute(String channel, CommandSender sender, String[] args);
}

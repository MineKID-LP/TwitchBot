package de.stylabs.twitchbot.commands;

import de.stylabs.twitchbot.TwitchBot;
import de.stylabs.twitchbot.commands.util.Command;
import de.stylabs.twitchbot.commands.util.CommandManager;
import de.stylabs.twitchbot.commands.util.CommandSender;

public class Help extends Command {

    public Help() {
        super("help", "Displays information about all commands", "!help <?command>");
    }

    @Override
    public void execute(String channel, CommandSender sender, String[] args) {
        CommandManager commandManager = TwitchBot.getCommandManager();
        if (args.length == 0) {
            TwitchBot.getClient().getChat().sendMessage(channel, "Available commands:");
            for (Command command : commandManager.getCommands().values()) {
                TwitchBot.getClient().getChat().sendMessage(channel, command.getName() + " - " + command.getDescription());
            }
            return;
        }

        Command command = commandManager.getCommand(args[0]);
        if (command == null) {
            TwitchBot.getClient().getChat().sendMessage(channel, "Invalid command: " + args[0]);
            return;
        }

        TwitchBot.getClient().getChat().sendMessage(channel, "Command: " + command.getName());
        TwitchBot.getClient().getChat().sendMessage(channel, "Description: " + command.getDescription());
        TwitchBot.getClient().getChat().sendMessage(channel, "Usage: " + command.getUsage());
    }
}

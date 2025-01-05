package de.stylabs.twitchbot.commands.util;

import de.stylabs.twitchbot.TwitchBot;

public class JoinCommand extends Command{

    public JoinCommand() {
        super("join", "Join Our Minecraft Server!", "join");
    }
    @Override
    public void execute(String channel, CommandSender sender, String[] args) {
        TwitchBot.getChat().sendMessage(channel, "Download the Modpack at https://modrinth.com/modpack/create-magic-tech, start Minecraft & Join magicand.tech! ");
    }
}

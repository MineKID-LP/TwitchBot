package de.stylabs.twitchbot.commands;

import de.stylabs.twitchbot.TwitchBot;
import de.stylabs.twitchbot.commands.util.Command;
import de.stylabs.twitchbot.commands.util.CommandSender;

public class PollCommand extends Command {

    public PollCommand() {
        super("poll", "Starts a poll", "poll <question> <option1> <option2> ...");
    }

    @Override
    public void execute(String channel, CommandSender sender, String[] args) {
        if(!sender.isBroadcaster() || !sender.isModerator()) {
            TwitchBot.getChat().sendMessage(channel, "You need to be a moderator to start a poll.");
            return;
        }


    }
}

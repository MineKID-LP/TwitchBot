package de.stylabs.twitchbot.commands.util;

import com.github.twitch4j.common.enums.CommandPermission;

import java.util.Set;

public record CommandSender(String name, Set<CommandPermission> roles) {

    public boolean isBroadcaster() {
        return roles.contains(CommandPermission.BROADCASTER);
    }

    public boolean isModerator() {
        return roles.contains(CommandPermission.MODERATOR);
    }

    public boolean isVIP() {
        return roles.contains(CommandPermission.VIP);
    }

    public boolean isSubscriber() {
        return roles.contains(CommandPermission.SUBSCRIBER);
    }

    public boolean isTwitchStaff() {
        return roles.contains(CommandPermission.TWITCHSTAFF);
    }

    public boolean isFounder() {
        return roles.contains(CommandPermission.FOUNDER);
    }
}
package de.stylabs.twitchbot;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.helix.domain.ChatUserColor;
import com.github.twitch4j.pubsub.domain.CreatorGoal;
import com.github.twitch4j.pubsub.events.CreatorGoalEvent;
import de.stylabs.twitchbot.commands.Help;
import de.stylabs.twitchbot.overlay.ChatOverlay;
import de.stylabs.twitchbot.overlay.FollowerOverlay;
import de.stylabs.twitchbot.overlay.WebServer;
import de.stylabs.twitchbot.commands.util.CommandManager;
import de.stylabs.twitchbot.commands.util.CommandSender;
import de.stylabs.twitchbot.util.SecretsManager;
import de.stylabs.twitchbot.util.TwitchCredential;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class TwitchBot {
    private static final Logger LOGGER = Logger.getLogger(TwitchBot.class.getName());
    private static final CommandManager commandManager = new CommandManager();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static TwitchClient twitchClient;
    private static TwitchCredential botCredential;
    private static TwitchCredential userCredential;

    public static void main(String[] args) {
        // Create an OAuth credential
        SecretsManager.load();

        botCredential = new TwitchCredential("bot");
        userCredential = new TwitchCredential("user");

        new Thread(() -> {
            WebServer.start();
        }).start();

        registerCommands();
        registerOverlays();

        // Build the Twitch client
        twitchClient = TwitchClientBuilder.builder()
                .withDefaultAuthToken(userCredential.getCredential())
                .withEnableChat(true) // Enables chat module
                .withEnableHelix(true) // Enables Helix API support
                .withEnablePubSub(true) // Enables PubSub for events like channel points
                .withChatAccount(botCredential.getCredential()) // Sets the account for chat
                .build();

        twitchClient.getClientHelper().enableStreamEventListener(getChannelName());
        twitchClient.getClientHelper().enableFollowEventListener(getChannelName());
        twitchClient.getClientHelper().enableClipEventListener(getChannelName());

        twitchClient.getChat().joinChannel(getChannelName());

        // Register an event listener for chat messages
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            String message = event.getMessage();
            String username = event.getMessageEvent().getUserDisplayName().orElse(event.getUser().getName());
            CommandSender sender = new CommandSender(username, event.getPermissions());
            if(!message.startsWith("!")) {
                ChatOverlay.onChatMessage(sender, message, event.getMessageEvent().getUserChatColor(), event.getMessageEvent().getTagValue("emotes").orElse(""));
                return;
            }

            String[] commandArguments = message.split(" ");
            String command = commandArguments[0].substring(1);
            commandArguments = Arrays.copyOfRange(commandArguments, 1, commandArguments.length);

            commandManager.runCommand(command, event.getChannel().getName(), sender, commandArguments);
        });

        twitchClient.getEventManager().onEvent(FollowEvent.class, event -> {
            String id = event.getUser().getId();
            String name = event.getUser().getName();
            List<ChatUserColor> colorList = twitchClient.getHelix().getUserChatColor(getChannelID(), Collections.singletonList(id)).execute().getData();
            String userColor = colorList.getFirst().getColor();
            String profilePictureUrl = twitchClient.getHelix().getUsers(null, Collections.singletonList(id), null).execute().getUsers().getFirst().getProfileImageUrl();
            FollowerOverlay.onFollow(name, userColor, profilePictureUrl);
        });

        twitchClient.getEventManager().onEvent(CreatorGoalEvent.class , event -> {
            CreatorGoal goal = event.getGoal();
            if(goal.getContributionType().equals(CreatorGoal.Type.FOLLOWERS)){
                FollowerOverlay.moveGoalPost(goal.getCurrentContributions(), goal.getTargetContributions());
            }
        });

        // Log connection success
        LOGGER.info("Twitch bot is running and connected to chat!");

        // Add more listeners and functionality below as needed
    }

    private static void registerOverlays() {
        WebServer.registerOverlay(new ChatOverlay());
    }

    private static void registerCommands() {
        commandManager.registerCommand("help", new Help());
    }

    public static TwitchClient getClient() {
        return twitchClient;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static String authenticatedRequest(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + botCredential.getCredential().getAccessToken())
                .header("Client-Id", SecretsManager.getClientId())
                .build();

        HttpClient client = HttpClient.newHttpClient();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();
    }

    public static String getChannelID() {
        return "277597877";
    }

    public static String getChannelName() {
        return "minekid_lp";
    }

    public static ScheduledExecutorService getExecutor() {
        return scheduler;
    }
}

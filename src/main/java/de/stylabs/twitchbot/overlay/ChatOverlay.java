package de.stylabs.twitchbot.overlay;

import de.stylabs.twitchbot.commands.util.CommandSender;
import de.stylabs.twitchbot.overlay.chat.ChatMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

import static de.stylabs.twitchbot.util.RandomShit.getRandomChatColor;

public class ChatOverlay extends Overlay {
    private static final HashMap<String, String> userColorMap = new HashMap<>();
    private static long lastMessage = 0;
    public ChatOverlay() {
        super("chat");
        InputStream inputStream = getClass().getResourceAsStream("/overlays/chat.html");
        try {
            assert inputStream != null;
            setHtml(new String(inputStream.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void onChatMessage(CommandSender sender, String message, Optional<String> userChatColor, String emotes) {
        String color = userChatColor.orElse("NO COLOR");

        if (color.equals("NO COLOR")) {
            color = userColorMap.getOrDefault(sender.name(), getRandomChatColor());
            userColorMap.put(sender.name(), color);
        }

        lastMessage++;
        ChatMessage chatMessage = new ChatMessage(sender.name(), color, message, lastMessage, emotes);
        sendUpdate(chatMessage.toJSON());
    }
}

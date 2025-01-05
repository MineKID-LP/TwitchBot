package de.stylabs.twitchbot.ai;

import de.stylabs.twitchbot.TwitchBot;
import de.stylabs.twitchbot.commands.util.CommandSender;
import de.stylabs.twitchbot.util.Config;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

@ServerEndpoint("/ai-ws")
public class AiBridge {
    private static final HashMap<String, Session> sessions = new HashMap<>();

    public static void onChatMessage(CommandSender sender, String message) {
        JSONObject event = new JSONObject();
        event.put("type", "chat_message");
        event.put("sender", sender.name());
        event.put("message", message);
        sendEvent(event);
    }

    @OnOpen
    public void onOpen(Session session) {
        if(!Config.isAIEnabled()) {
            try {
                session.close();
            } catch (IOException _) {}
            return;
        }

        TwitchBot.getLogger().info("[AI] WebSocket opened: " + session.getId());
        sessions.put(session.getId(), session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject event = new JSONObject(message);
        String path = event.getString("path");
        if(path.isBlank() || path.isEmpty()) return;
        SoundPlayer.playSound(path, System.currentTimeMillis());
    }

    @OnClose
    public void onClose(Session session) {
        TwitchBot.getLogger().info("[AI] WebSocket closed: " + session.getId());
        sessions.remove(session.getId());
    }

    public static void sendEvent(JSONObject event) {
        String msg = event.toString();
        sessions.forEach((id, session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (Exception e) {
                TwitchBot.getLogger().severe(e.getMessage());
            }
        });
    }
}
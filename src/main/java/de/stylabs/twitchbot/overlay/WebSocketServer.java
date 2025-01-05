package de.stylabs.twitchbot.overlay;

import de.stylabs.twitchbot.TwitchBot;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.util.HashMap;

@ServerEndpoint("/ws")
public class WebSocketServer {
    private static final HashMap<String, Session> sessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        TwitchBot.getLogger().info("WebSocket opened: " + session.getId());
        sessions.put(session.getId(), session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        TwitchBot.getLogger().info("Message from " + session.getId() + ": " + message);
    }

    @OnClose
    public void onClose(Session session) {
        TwitchBot.getLogger().info("WebSocket closed: " + session.getId());
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

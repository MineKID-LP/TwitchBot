package de.stylabs.twitchbot.overlay.chat;

import org.json.JSONObject;

public record ChatMessage(String sender, String color, String message, long id, String emotes) {
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", sender);
        jsonObject.put("color", color);
        jsonObject.put("message", message);
        jsonObject.put("id", id);
        jsonObject.put("emotes", emotes);
        return jsonObject;
    }
}

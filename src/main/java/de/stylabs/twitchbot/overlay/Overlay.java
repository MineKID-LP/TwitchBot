package de.stylabs.twitchbot.overlay;

import org.json.JSONObject;

public abstract class Overlay {
    private static String name;
    private String html;

    public Overlay(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setHtml(String html) {
        this.html = html.replaceAll("%URL%", WebServer.boundAddress());
    }

    public String getHtml() {
        return html;
    }

    protected static void sendUpdate(JSONObject update) {
        update.put("overlay", name);
        WebSocketServer.sendEvent(update);
    }
}
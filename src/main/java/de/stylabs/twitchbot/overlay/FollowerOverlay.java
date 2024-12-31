package de.stylabs.twitchbot.overlay;

import org.json.JSONObject;

public class FollowerOverlay extends Overlay {
    public FollowerOverlay() {
        super("follower");
    }

    public static void onFollow(String name, String userColor, String profilePictureUrl) {
        JSONObject update = new JSONObject();
        update.put("name", name);
        update.put("color", userColor);
        update.put("profilePictureUrl", profilePictureUrl);
        update.put("event", "follow");
        sendUpdate(update);
    }

    public static void moveGoalPost(Integer currentContributions, Integer targetContributions) {
        JSONObject update = new JSONObject();
        update.put("followrs", currentContributions);
        update.put("goal", targetContributions);
        update.put("event", "goal");
        sendUpdate(update);
    }
}

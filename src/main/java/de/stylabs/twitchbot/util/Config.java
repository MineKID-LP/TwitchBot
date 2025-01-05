package de.stylabs.twitchbot.util;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class Config {
    private static final File configFile = new File("config.json");
    private static boolean isAIEnabled = false;

    public static boolean isAIEnabled() {
        return isAIEnabled;
    }

    public static void loadConfig() {
        if (!configFile.exists()) {
            saveDefault();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(new String(Files.readAllBytes(configFile.toPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        isAIEnabled = jsonObject.getBoolean("ai_enabled");
    }

    private static void saveDefault() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ai_enabled", false);
        try {
            Files.write(configFile.toPath(), jsonObject.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

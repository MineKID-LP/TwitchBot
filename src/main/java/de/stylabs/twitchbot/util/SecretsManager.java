package de.stylabs.twitchbot.util;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class SecretsManager {
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static final File keyFile = new File("credentials/secrets.json");

    public static void load() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(keyFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new String(fileInputStream.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CLIENT_ID = jsonObject.getString("client_id");
        CLIENT_SECRET = jsonObject.getString("client_secret");
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getClientSecret() {
        return CLIENT_SECRET;
    }
}

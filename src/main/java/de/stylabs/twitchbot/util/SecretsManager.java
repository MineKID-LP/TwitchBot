package de.stylabs.twitchbot.util;

import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;

public class SecretsManager {
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static final File keyFile = new File("credentials/secrets.json");

    public static void load() throws Exception {
        if(!keyFile.exists()) {
            saveDefault();
            throw new Exception("Please fill in the client_id and client_secret in the file credentials/secrets.json");
        }
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

    private static void saveDefault() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", "YOUR_CLIENT_ID");
        jsonObject.put("client_secret", "YOUR_CLIENT_SECRET");
        try {
            Files.write(keyFile.toPath(), jsonObject.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getClientSecret() {
        return CLIENT_SECRET;
    }
}

package de.stylabs.twitchbot.util;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import de.stylabs.twitchbot.TwitchBot;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchCredential {
    private final String name;
    private final File keyFile;
    private String REFRESH_TOKEN;
    private String ACCESS_TOKEN;
    private long expiresIn;
    private long expiresAt;

    public TwitchCredential(String name) {
        this.name = name;
        this.keyFile = new File("credentials/" + name + ".json");
        refreshToken();
    }

    public void refreshToken() {
        TwitchBot.getLogger().info("Getting token..");
        load();
        expiresAt = new Date().getTime() + expiresIn * 1000;
        TwitchBot.getLogger().info((expiresAt > new Date().getTime()) + ", " + expiresAt + ", " + new Date().getTime());
        if (expiresAt > new Date().getTime()) return;
        TwitchBot.getLogger().info("Refreshing token..");
        // Refresh the token
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://id.twitch.tv/oauth2/token"))
                .POST(HttpRequest.BodyPublishers.ofString(String.format(
                        "grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s",
                        REFRESH_TOKEN, SecretsManager.getClientId(), SecretsManager.getClientSecret()
                )))
                .build();

        TwitchBot.getLogger().info("Requesting token..");

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TwitchBot.getLogger().info("Token received..");
            parseToken(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void parseToken(String response) {
        String[] parts = response.split(",");
        for (String part : parts) {
            if (part.contains("access_token")) {
                ACCESS_TOKEN = part.split(":")[1].replace("\"", "").replace("}", "");
            } else if (part.contains("expires_in")) {
                expiresIn = Long.parseLong(part.split(":")[1].replace("\"", "").replace("}", ""));
            }
            expiresAt = new Date().getTime() + expiresIn * 1000;
        }

        ScheduledExecutorService executor = TwitchBot.getExecutor();
        executor.schedule(() -> {
            System.exit(0);
        }, expiresAt - new Date().getTime(), TimeUnit.MILLISECONDS);
        TwitchBot.getLogger().info("Token expires at: " + new Date(expiresAt).toString());
        save();
    }

    private void load () {
        // Load the tokens from a file or database
        if(!keyFile.exists()) save();

        // Load the tokens from the file
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(keyFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String content;
        try {
            content = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject(content);
        ACCESS_TOKEN = jsonObject.getString("access_token");
        expiresIn = jsonObject.getLong("expires_in");
        REFRESH_TOKEN = jsonObject.getString("refresh_token");
        expiresAt = jsonObject.optLong("expires_at", new Date().getTime() + expiresIn * 1000);

        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save () {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(keyFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("access_token", ACCESS_TOKEN);
        jsonObject.put("expires_in", expiresIn);
        jsonObject.put("refresh_token", REFRESH_TOKEN);
        jsonObject.put("expires_at", expiresAt);

        try {
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OAuth2Credential getCredential() {
        return new OAuth2Credential("twitch", ACCESS_TOKEN);
    }
}

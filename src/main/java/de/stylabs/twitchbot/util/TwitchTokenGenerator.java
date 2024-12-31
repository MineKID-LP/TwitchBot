package de.stylabs.twitchbot.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.sun.net.httpserver.HttpServer;
import de.stylabs.twitchbot.TwitchBot;
import org.json.JSONObject;

public class TwitchTokenGenerator {

    private static final String CLIENT_ID = SecretsManager.getClientId(); // Replace with your Client ID
    private static final String CLIENT_SECRET = SecretsManager.getClientSecret(); // Replace with your Client Secret
    private static final String REDIRECT_URI = "http://localhost:5000"; // Replace with your Redirect URI
    private static final String SCOPES = "analytics:read:extensions+user:edit+user:read:email+clips:edit+bits:read+analytics:read:games+user:edit:broadcast+user:read:broadcast+chat:read+chat:edit+channel:moderate+channel:read:subscriptions+whispers:read+whispers:edit+moderation:read+channel:read:redemptions+channel:edit:commercial+channel:read:hype_train+channel:read:stream_key+channel:manage:extensions+channel:manage:broadcast+user:edit:follows+channel:manage:redemptions+channel:read:editors+channel:manage:videos+user:read:blocked_users+user:manage:blocked_users+user:read:subscriptions+user:read:follows+channel:manage:polls+channel:manage:predictions+channel:read:polls+channel:read:predictions+moderator:manage:automod+channel:manage:schedule+channel:read:goals+moderator:read:automod_settings+moderator:manage:automod_settings+moderator:manage:banned_users+moderator:read:blocked_terms+moderator:manage:blocked_terms+moderator:read:chat_settings+moderator:manage:chat_settings+channel:manage:raids+moderator:manage:announcements+moderator:manage:chat_messages+user:manage:chat_color+channel:manage:moderators+channel:read:vips+channel:manage:vips+user:manage:whispers+channel:read:charity+moderator:read:chatters+moderator:read:shield_mode+moderator:manage:shield_mode+moderator:read:shoutouts+moderator:manage:shoutouts+moderator:read:followers+channel:read:guest_star+channel:manage:guest_star+moderator:read:guest_star+moderator:manage:guest_star+channel:bot+user:bot+user:read:chat+channel:manage:ads+channel:read:ads+user:read:moderated_channels+user:write:chat+user:read:emotes+moderator:read:unban_requests+moderator:manage:unban_requests+moderator:read:suspicious_users+moderator:manage:warnings"; // Add additional scopes as needed

    public static void main(String[] args) throws Exception {
        // Step 1: Generate the authorization URL
        SecretsManager.load();
        String authUrl = String.format(
                "https://id.twitch.tv/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=%s",
                CLIENT_ID,
                REDIRECT_URI,
                SCOPES
        );

        System.out.println("Open the following URL in your browser to authorize the application:");
        System.out.println(authUrl);

        // Step 2: Start a local server to handle the redirect
        System.out.println("Waiting for authorization...");
        String authCode = startHttpServer();

        // Step 3: Exchange authorization code for an access token
        String token = getAccessToken(authCode);
        System.out.println("Access Token: " + token);
    }

    private static String startHttpServer() throws IOException {
        final String[] authCode = new String[1];
        InetSocketAddress address = new InetSocketAddress(5000);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext("/", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String response = "Authorization successful! You can close this tab.";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            // Extract the authorization code
            if (query != null && query.contains("code=")) {
                authCode[0] = query.split("code=")[1].split("&")[0];
            }

            server.stop(0);
        });

        server.start();
        while (authCode[0] == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return authCode[0];
    }

    private static String getAccessToken(String authCode) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://id.twitch.tv/oauth2/token"))
                .POST(HttpRequest.BodyPublishers.ofString(String.format(
                        "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                        CLIENT_ID, CLIENT_SECRET, authCode, REDIRECT_URI
                )))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get access token: " + response.body());
        }

        // Parse the response to extract the access token
        TwitchBot.getLogger().info("Response: " + response.body());
        JSONObject json = new JSONObject(response.body());
        return json.get("access_token").toString();
    }
}


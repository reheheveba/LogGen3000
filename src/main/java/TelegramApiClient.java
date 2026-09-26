package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Responsible only for talking to the Telegram Bot API over HTTPS.
 * Knows nothing about login generation - only sending/receiving messages.
 */
public class TelegramApiClient {

    private final HttpClient client;
    private final String token;

    public TelegramApiClient(String token) {
        this.token = token;
        this.client = HttpClient.newHttpClient();
    }

    /**
     * Fetches new updates (messages) using long polling.
     * @param offset id of the last unprocessed update
     * @return JSON array of update objects
     */
    public JSONArray getUpdates(long offset) throws Exception {
        String url = "https://api.telegram.org/bot" + token
                + "/getUpdates?offset=" + offset + "&timeout=30";

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(40))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        if (!json.getBoolean("ok")) {
            throw new RuntimeException("Telegram API returned an error: " + json);
        }

        return json.getJSONArray("result");
    }

    /**
     * Sends a text message to a chat.
     * @param chatId chat id to send to
     * @param text message text
     */
    public void sendMessage(long chatId, String text) throws Exception {
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";
        String body = "chat_id=" + chatId + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        if (!json.getBoolean("ok")) {
            System.err.println("Failed to send message: " + json);
        }
    }
}

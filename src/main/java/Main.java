package org.example;


import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    private static final String TOKEN = "THE TOKEN NAME"; //u musst insert your token:)

    public static void main(String[] args) {
        org.example.TelegramApiClient api = new org.example.TelegramApiClient(TOKEN);
        org.example.LoginGenerator generator = new org.example.LoginGenerator();
        long offset = 0;

        System.out.println("Bot started and listening for updates...");

        while (true) {
            try {
                JSONArray updates = api.getUpdates(offset);

                for (int i = 0; i < updates.length(); i++) {
                    JSONObject update = updates.getJSONObject(i);
                    offset = update.getLong("update_id") + 1;

                    if (!update.has("message")) {
                        continue;
                    }

                    JSONObject message = update.getJSONObject("message");
                    if (!message.has("text")) {
                        continue;
                    }

                    long chatId = message.getJSONObject("chat").getLong("id");
                    String text = message.getString("text");

                    handleMessage(api, generator, chatId, text);
                }

            } catch (Exception e) {
                System.err.println("Error in update loop: " + e.getMessage());
                sleep(3000);
            }
        }
    }

    private static void handleMessage(org.example.TelegramApiClient api, org.example.LoginGenerator generator,
                                      long chatId, String text) throws Exception {
        switch (text) {
            case "/start" -> api.sendMessage(chatId,
                    "Hi! Type /gen1 for a Word_Word login, or /gen2 for a WordWord login.");

            case "/gen1" -> {
                String login = generator.generate();
                api.sendMessage(chatId, "Your login: " + login);
            }

            case "/gen2" -> {
                String login = generator.generateNoSeparator();
                api.sendMessage(chatId, "Your login: " + login);
            }
            case "/gen3" -> {
                String login = generator.generate();
                api.sendMessage(chatId, "Your login: " + login);
            }


            default -> api.sendMessage(chatId,
                    "Unknown command. Available commands: /start, /gen1, /gen2");
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
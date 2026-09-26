package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginGenerator {

    private static final String[] FALLBACK_WORDS = {
            "Falcon", "Harbor", "Comet", "Silver", "Meadow", "Rocket", "Crystal", "Shadow",
            "Golden", "Thunder", "Ocean", "Maple", "Winter", "Phoenix", "Copper", "Velvet"
    };

    private final String[] words;
    private final Set<String> usedLogins = new HashSet<>();

    public LoginGenerator() {
        this.words = loadWords();
        System.out.println("Loaded " + words.length + " words for login generation.");
    }

    public synchronized String generate() {
        String login;
        do {
            login = pickTwoWords("_");
        } while (usedLogins.contains(login));

        usedLogins.add(login);
        return login;
    }
    public synchronized String generateSingleWord(){
        String login;
        do{
            login = pickOneWords("");

        }while (usedLogins.contains(login));
        usedLogins.add(login);
        return login;
    }
    public synchronized String generateNoSeparator() {
        String login;
        do {
            login = pickTwoWords("");
        } while (usedLogins.contains(login));

        usedLogins.add(login);
        return login;
    }

    private String pickTwoWords(String separator) {
        String first = words[(int) (Math.random() * words.length)];
        String second = words[(int) (Math.random() * words.length)];
        return first + separator + second;
    }
    private String pickOneWords(String separator) {
        String first = words[(int) (Math.random() * words.length)];
        return first + separator;
    }

    private String[] loadWords() {
        List<String> result = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream("/words.txt")) {
            if (is == null) {
                System.err.println("words.txt not found in resources, using fallback word list.");
                return FALLBACK_WORDS;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim();

                    if (word.length() < 3 || word.length() > 10) {
                        continue;
                    }
                    if (!word.chars().allMatch(Character::isLetter)) {
                        continue;
                    }

                    String capitalized = word.substring(0, 1).toUpperCase()
                            + word.substring(1).toLowerCase();
                    result.add(capitalized);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read words.txt, using fallback word list: " + e.getMessage());
            return FALLBACK_WORDS;
        }

        if (result.isEmpty()) {
            return FALLBACK_WORDS;
        }

        return result.toArray(new String[0]);
    }
}
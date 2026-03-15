package org.aliexpressApp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TokenManager {

    private static final String TOKEN_FILE_PATH = "token.json";

    /**
     * Saves the entire token JSON object to a file.
     * @param tokenJsonObject The JSON object received from the token creation API call.
     */
    public static void saveToken(JsonObject tokenJsonObject) {
        // Add a timestamp to know when the token was saved
        tokenJsonObject.addProperty("save_timestamp_ms", System.currentTimeMillis());

        try (FileWriter writer = new FileWriter(TOKEN_FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(tokenJsonObject));
            System.out.println("Token details successfully saved to " + TOKEN_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error saving token to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a valid access token from the file.
     * @return The access token string if it exists and is not expired, otherwise null.
     */
    public static String loadValidAccessToken() {
        try (FileReader reader = new FileReader(TOKEN_FILE_PATH)) {
            JsonObject tokenData = JsonParser.parseReader(reader).getAsJsonObject();

            long saveTimestamp = tokenData.get("save_timestamp_ms").getAsLong();
            // expire_in is in seconds, convert to milliseconds
            long expiresInMs = tokenData.get("expires_in").getAsLong() * 1000;
            long expiryTime = saveTimestamp + expiresInMs;

            // Add a 5-minute buffer to be safe
            if (expiryTime > System.currentTimeMillis() + (5 * 60 * 1000)) {
                System.out.println("Valid access token loaded from file.");
                return tokenData.get("access_token").getAsString();
            }
            else {
                System.err.println("Access token from file has expired.");
                return null;
            }
        } catch (IOException | NullPointerException e) {
            // File not found, is not a valid JSON, or fields are missing
            System.err.println("Could not load token from file. File may not exist or is invalid.");
            return null;
        }
    }
}

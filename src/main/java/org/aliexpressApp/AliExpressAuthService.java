package org.aliexpressApp;

import com.global.iop.api.IopClient;
import com.global.iop.api.IopClientImpl;
import com.global.iop.api.IopRequest;
import com.global.iop.api.IopResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;

public class AliExpressAuthService {

    private static final String AUTH_API_URL = "https://api-sg.aliexpress.com";
    private static final String OAUTH_BASE_URL = "https://api-sg.aliexpress.com/oauth/authorize";

    // IMPORTANT: You MUST set this in your AliExpress Developer Console.
    // It must exactly match the URL you provide here.
    private static final String REDIRECT_URI = "https://productdeals123.blogspot.com/2026/03/judaicads-automated-inventory-order.html";


    /**
     * Generates and prints the authorization URL for the user to visit.
     * @param appKey The application's App Key.
     * @return The fully constructed URL.
     */
    public static String generateAuthorizationUrl(String appKey) {
        // The 'state' parameter is for security and can be any random string.
        return OAUTH_BASE_URL + "?response_type=code" +
                "&client_id=" + appKey +
                "&redirect_uri=" + REDIRECT_URI +
                "&force_auth=true";
    }

    /**
     * Extracts the 'code' parameter from a full redirect URL.
     * @param fullUrl The full URL the user was redirected to after authorization.
     * @return The extracted code, or null if not found.
     */
    public static String extractCodeFromUrl(String fullUrl) {
        try {
            // Use URL parsing to find the 'code' query parameter
            java.net.URL url = new java.net.URL(fullUrl);
            String query = url.getQuery();
            if (query == null) return null;

            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], "UTF-8");
                if ("code".equals(key)) {
                    return URLDecoder.decode(pair[1], "UTF-8");
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing redirect URL: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        final String APP_KEY = dotenv.get("APP_KEY");
        final String APP_SECRET = dotenv.get("SECRET_KEY");

        // 1. Generate and display the URL
        String authUrl = generateAuthorizationUrl(APP_KEY);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("STEP 1: Please open the following URL in your web browser:");
        System.out.println(authUrl);
        System.out.println("--------------------------------------------------------------------------------");

        // 2. Wait for user to paste the redirect URL
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nSTEP 2: After authorizing, paste the full redirect URL here and press Enter:");
        String redirectUrl = scanner.nextLine();

        // 3. Extract the code
        String authCode = extractCodeFromUrl(redirectUrl);
        if (authCode == null) {
            System.err.println("Could not find 'code' in the provided URL. Please try again.");
            return;
        }
        System.out.println("Successfully extracted authorization code: " + authCode);

        // 4. Exchange the code for an access token
        System.out.println("\nSTEP 3: Exchanging code for access token...");
        IopClient client = new IopClientImpl(AUTH_API_URL, APP_KEY, APP_SECRET);
        IopRequest request = new IopRequest();
        request.setApiName("/auth/token/create");
        request.addApiParameter("code", authCode);

        try {
            IopResponse response = client.execute(request);

            if (response.isSuccess()) {
                String responseBody = response.getGopResponseBody();
                System.out.println("API Response: " + responseBody);

                // 5. Save the token details to a file
                JsonObject tokenJson = JsonParser.parseString(responseBody).getAsJsonObject();
                TokenManager.saveToken(tokenJson);

            } else {
                System.err.println("Failed to get access token!");
                System.err.println("Error: " + response.getGopErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

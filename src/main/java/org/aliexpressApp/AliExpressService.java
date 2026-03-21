package org.aliexpressApp;

import com.global.iop.api.IopClient;
import com.global.iop.api.IopClientImpl;
import com.global.iop.api.IopRequest;
import com.global.iop.api.IopResponse;
import com.global.iop.domain.Protocol;

import io.github.cdimascio.dotenv.Dotenv;

public class AliExpressService {

    private static final String API_URL = "https://api-sg.aliexpress.com/sync/";

    public static void main(String[] args) {
        // Step 1: Load credentials from .env file
        Dotenv dotenv = Dotenv.load();
        final String APP_KEY = dotenv.get("APP_KEY");
        final String APP_SECRET = dotenv.get("SECRET_KEY");

        // Step 2: Load the access token from the saved file
        System.out.println("Attempting to load access token from file...");
        String accessToken = TokenManager.loadValidAccessToken();

        // Step 3: Check if the token is valid. If not, guide the user.
        if (accessToken == null) {
            System.err.println("----------------------------------------------------------------------");
            System.err.println("No valid access token found.");
            System.err.println("Please run the 'AliExpressAuthService' class first to generate a new token.");
            System.err.println("----------------------------------------------------------------------");
            return; // Halt execution
        }

        System.out.println("Access token loaded successfully. Fetching product details...");

        // Step 4: Proceed with the API call using the valid token
        IopClient client = new IopClientImpl(API_URL, APP_KEY, APP_SECRET);
        IopRequest request = new IopRequest();
        request.setApiName("aliexpress.ds.product.wholesale.get");
        request.addApiParameter("ship_to_country", "US");
        request.addApiParameter("product_id", "1005008144946275");
        request.addApiParameter("target_currency", "USD");
        request.addApiParameter("target_language", "en");
        request.addApiParameter("remove_personal_benefit", "false");
        System.out.println("API Name: " + request.getApiName());

        try {
            IopResponse response = client.execute(request, accessToken, Protocol.TOP);

            if (response.isSuccess()) {
                System.out.println("\nSuccess! Product Data:");
                String resp = response.getGopResponseBody();
                System.out.println(resp);

            } else {
                System.err.println("\nAPI call to get product details failed!");
                System.err.println("Error Type: " + response.getGopErrorType());
                System.err.println("Error Code: " + response.getGopErrorCode());
                System.err.println("Message: " + response.getGopErrorMessage());
                System.err.println("Sub Message: " + response.getGopErrorSubMsg());
                System.err.println("Request URL: " + response.getGopRequestUrl());
                System.err.println("Request Parameters: " + response.getGopRequestParams());
            }
            Thread.sleep(10);
        } catch (Exception e) {
            System.err.println("\nAn exception occurred while trying to get product details.");
            e.printStackTrace();
        }
    }
}
package org.aliexpressApp;

import com.global.iop.api.IopClient;
import com.global.iop.api.IopClientImpl;
import com.global.iop.api.IopRequest;
import com.global.iop.api.IopResponse;
import com.global.iop.domain.Protocol;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Arrays;
import java.util.List;

public class AliExpressService {

    private static final String API_URL = "https://api-sg.aliexpress.com/sync/";
    private final String appKey;
    private final String appSecret;
    private final IopClient client;

    public AliExpressService() {
        Dotenv dotenv = Dotenv.load();
        this.appKey = dotenv.get("APP_KEY");
        this.appSecret = dotenv.get("SECRET_KEY");
        this.client = new IopClientImpl(API_URL, this.appKey, this.appSecret);
    }

    private String loadAccessToken() {
        System.out.println("Attempting to load access token from file...");
        String accessToken = TokenManager.loadValidAccessToken();
        if (accessToken == null) {
            System.err.println("----------------------------------------------------------------------");
            System.err.println("No valid access token found.");
            System.err.println("Please run the 'AliExpressAuthService' class first to generate a new token.");
            System.err.println("----------------------------------------------------------------------");
        } else {
            System.out.println("Access token loaded successfully.");
        }
        return accessToken;
    }

    private IopRequest createRequest(String apiName, List<String> params) {
        if (params.size() % 2 != 0) {
            throw new IllegalArgumentException("Parameters list must contain key-value pairs.");
        }
        IopRequest request = new IopRequest();
        request.setApiName(apiName);
        for (int i = 0; i < params.size(); i += 2) {
            request.addApiParameter(params.get(i), params.get(i + 1));
        }
        System.out.println("API Name: " + request.getApiName());
        return request;
    }

    public String execute(String apiName, List<String> params) throws Exception {
        String accessToken = loadAccessToken();
        if (accessToken == null) {
            throw new Exception("Execution halted: No valid access token.");
        }

        IopRequest request = createRequest(apiName, params);

        try {
            IopResponse response = client.execute(request, accessToken, Protocol.TOP);
            if (response.isSuccess()) {
                System.out.println("\nSuccess! API Response:");
                return response.getGopResponseBody();
            } else {
                String errorMessage = "API call failed! Error: " + response.getGopErrorMessage() +
                                      " | Sub Error: " + response.getGopErrorSubMsg();
                System.err.println(errorMessage);
                throw new Exception(errorMessage);
            }
        } catch (Exception e) {
            System.err.println("\nAn exception occurred during API execution.");
            e.printStackTrace();
            throw e;
        }
    }
    
    public String execute(String command, String params) throws Exception {
        List<String> paramList = Arrays.asList(params.split("\s*,\s*"));
        return execute(command, paramList);
    }


    public static void main(String[] args) {
        AliExpressService aliExpressService = new AliExpressService();
        String apiName = "aliexpress.ds.product.wholesale.get";
        List<String> params = Arrays.asList(
                "ship_to_country", "US",
                "product_id", "1005008144946275",
                "target_currency", "USD",
                "target_language", "en",
                "remove_personal_benefit", "false"
        );

        try {
            String response = aliExpressService.execute(apiName, params);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package org.aliexpressApp;


import com.aliexpress.open.request.AliexpressAffiliateProductSkuDetailGetRequest;
import com.aliexpress.open.request.AliexpressAffiliateProductdetailGetRequest;
import com.global.iop.api.IopClient;
import com.global.iop.api.IopClientImpl;
import com.global.iop.api.IopResponse;

public class AliExpressService {
    private static final String URL = "https://api-sgaliexpress.com/sync";
    private static final String APP_KEY = "APP_KEY";
    private static final String APP_SECRET = "SECRET_KEY";

    public static void main(String[] args) {

        IopClient client = new IopClientImpl(URL, APP_KEY, APP_SECRET);
        AliexpressAffiliateProductdetailGetRequest req = new AliexpressAffiliateProductdetailGetRequest();

        req.addApiParameter("product_ids", "100500123456789");
        req.addApiParameter("target_currency", "USD");
        req.addApiParameter("target_language", "EN");

        try {
            IopResponse response = client.execute(req);

            if (response.isSuccess()) {
                System.out.println("Success! Data: ");
                System.out.println(response.getGopResponseBody());
            }
            else {
                System.out.println("Error Type: " + response.getGopErrorType());
                System.out.println("Error Code: " + response.getGopErrorCode());
                System.out.println("Message: " + response.getGopErrorMessage());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

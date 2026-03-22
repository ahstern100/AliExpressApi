package org.aliexpressApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiCommandRepository {

    private final List<ApiCommand> commands = new ArrayList<>();

    public ApiCommandRepository() {
        commands.add(new ApiCommand(
                "aliexpress.affiliate.product.query",
                "חיפוש מוצרים של שותפים",
                Arrays.asList(
                        new ApiParameter("app_signature", "חתימת אפליקציה", ""),
                        new ApiParameter("category_ids", "מזהי קטגוריות", ""),
                        new ApiParameter("fields", "שדות להחזרה", "commission_rate,sale_price"),
                        new ApiParameter("keywords", "מילות מפתח", ""),
                        new ApiParameter("max_sale_price", "מחיר מכירה מקסימלי", ""),
                        new ApiParameter("min_sale_price", "מחיר מכירה מינימלי", ""),
                        new ApiParameter("page_no", "מספר עמוד", "1"),
                        new ApiParameter("page_size", "גודל עמוד", "20"),
                        new ApiParameter("platform_product_type", "סוג מוצר פלטפורמה", ""),
                        new ApiParameter("sort", "סוג מיון", ""),
                        new ApiParameter("target_currency", "מטבע יעד", "USD"),
                        new ApiParameter("target_language", "שפת יעד", "en"),
                        new ApiParameter("tracking_id", "מזהה מעקב", ""),
                        new ApiParameter("ship_to_country", "מדינת משלוח", "US")
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.product.wholesale.get",
                "קבלת פרטי מוצר בסיטונאות",
                Arrays.asList(
                        new ApiParameter("ship_to_country", "מדינת משלוח", "US"),
                        new ApiParameter("product_id", "מזהה מוצר", "1005008144946275"),
                        new ApiParameter("target_currency", "מטבע יעד", "USD"),
                        new ApiParameter("target_language", "שפת יעד", "en"),
                        new ApiParameter("remove_personal_benefit", "הסר הטבה אישית", "false")
                )
        ));
    }

    public List<ApiCommand> getCommands() {
        return commands;
    }

    public ApiCommand findCommandByName(String name) {
        for (ApiCommand command : commands) {
            if (command.getName().equals(name)) {
                return command;
            }
        }
        return null;
    }
}

package org.aliexpressApp;

import org.aliexpressApp.dto.FreightCalculateForBuyerDTO;
import org.aliexpressApp.dto.ImageSearchV2DTO;
import org.aliexpressApp.dto.PlaceOrderRequestDTO;
import org.aliexpressApp.dto.QueryDeliveryReqDTO;
import org.aliexpressApp.dto.SingleOrderQueryDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        commands.add(new ApiCommand(
                "aliexpress.ds.product.get",
                "שאילתת פרטי מוצר עבור דרופשיפינג",
                Arrays.asList(
                        new ApiParameter("ship_to_country", "מדינה למשלוח", ""),
                        new ApiParameter("product_id", "מזהה מוצר", ""),
                        new ApiParameter("target_currency", "מטבע יעד", "USD"),
                        new ApiParameter("target_language", "שפת יעד", "en"),
                        new ApiParameter("remove_personal_benefit", "הסר הטבה אישית", "false"),
                        new ApiParameter("biz_model", "מודל עסקי (BETA)", ""),
                        new ApiParameter("province_code", "קוד מחוז/מדינה", ""),
                        new ApiParameter("city_code", "קוד עיר", "")
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.category.get",
                "קבלת מזהה ושם של קטגוריות",
                 Arrays.asList(
                        new ApiParameter("categoryId", "מזהה קטגוריה", ""),
                        new ApiParameter("language", "שפה", "en"),
                        new ApiParameter("app_signature", "חתימת אפליקציה", "")
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.order.create",
                "יצירת הזמנה ותשלום ב-DS",
                Arrays.asList(
                    new ApiParameter("param_place_order_request4_open_api_d_t_o", "פרטי הזמנה", PlaceOrderRequestDTO.class),
                    new ApiParameter("promotion_channel_info", "מידע על ערוץ הקידום", ""),
                    new ApiParameter("promotion_activity_id", "מזהה פעילות קידום", ""),
                    new ApiParameter("pay_currency", "מטבע תשלום", "USD"),
                    new ApiParameter("business_model", "מודל עסקי (wholesale/retail)", "retail"),
                    new ApiParameter("logistics_service_name", "שם שירות לוגיסטי", ""),
                    new ApiParameter("order_memo", "הערות להזמנה", "")
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.freight.query",
                "שאילתת משלוח - קבלת מידע על משלוח כולל מחיר ושיטות זמינות",
                Arrays.asList(
                        new ApiParameter("queryDeliveryReq", "בקשת שאילתת משלוח", QueryDeliveryReqDTO.class)
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.order.tracking.get",
                "קבלת מידע מעקב אחר הזמנת דרופשיפ",
                Arrays.asList(
                        new ApiParameter("ae_order_id", "מזהה הזמנה", ""),
                        new ApiParameter("language", "שפה", "en")
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.feed.itemids.get",
                "אחזור פריטים עם שם פיד במודל פשוט",
                Arrays.asList(
                        new ApiParameter("page_size", "מספר פריטים בעמוד", ""),
                        new ApiParameter("category_id", "מזהה קטגוריה ראשית", ""),
                        new ApiParameter("feed_name", "שם הפיד (ניתן לקבל מ-aliexpress.ds.feedname.get)", ""),
                        new ApiParameter("search_id", "מזהה חיפוש", "")
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.logistics.buyer.freight.calculate",
                "ממשק חישוב משלוח הניתן לקונים",
                Arrays.asList(
                        new ApiParameter("param_aeop_freight_calculate_for_buyer_d_t_o", "פרמטרים לבקשת חישוב משלוח", FreightCalculateForBuyerDTO.class)
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.image.searchV2",
                "חיפוש תמונות דרופשיפינג v2",
                Arrays.asList(
                        new ApiParameter("param0", "פרמטרים לחיפוש תמונה", ImageSearchV2DTO.class)
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.trade.ds.order.get",
                "שאילתת קונים לפרטי הזמנה עבור דרופשיפר",
                Arrays.asList(
                        new ApiParameter("single_order_query", "תנאי שאילתת הזמנה", SingleOrderQueryDTO.class)
                )
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.member.benefit.get",
                "שאילתה לקבלת פרטי הטבות לחבר",
                Collections.emptyList()
        ));

        commands.add(new ApiCommand(
                "aliexpress.ds.product.specialinfo.get",
                "קבלת מידע מיוחד על מוצרים (כמו אישורים)",
                Arrays.asList(
                        new ApiParameter("itemId", "מזהה מוצר", ""),
                        new ApiParameter("countryCodes", "קודי מדינות (מופרד בפסיקים)", ""),
                        new ApiParameter("appKey", "מפתח האפליקציה שלך", "")
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

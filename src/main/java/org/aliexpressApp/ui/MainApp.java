package org.aliexpressApp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.aliexpressApp.ApiCommandRepository;
import org.aliexpressApp.AliExpressService;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Dotenv dotenv = Dotenv.load();
        String appKey = dotenv.get("APP_KEY");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        ApiCommandRepository apiCommandRepository = new ApiCommandRepository();
        AliExpressService aliExpressService = new AliExpressService();

        primaryStage.setTitle("AliExpress API Client");

        TabPane tabPane = new TabPane();

        Tab tokenTab = new Tab("Generate Token");
        tokenTab.setContent(new TokenTabPane(appKey));
        tokenTab.setClosable(false);

        Tab apiTab = new Tab("Execute API");
        apiTab.setContent(new ApiTabPane(apiCommandRepository, aliExpressService, objectMapper));
        apiTab.setClosable(false);

        tabPane.getTabs().addAll(tokenTab, apiTab);

        VBox vbox = new VBox(tabPane);
        Scene scene = new Scene(vbox, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

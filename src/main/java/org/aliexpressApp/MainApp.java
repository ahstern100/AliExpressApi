package org.aliexpressApp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

public class MainApp extends Application {

    private final AliExpressAuthService authService = new AliExpressAuthService();
    private final AliExpressService aliExpressService = new AliExpressService();
    private final ApiCommandRepository apiCommandRepository = new ApiCommandRepository();
    private String appKey;


    // To hold the dynamically created text fields for parameters
    private final Map<String, TextField> parameterTextFields = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Dotenv dotenv = Dotenv.load();
        this.appKey = dotenv.get("APP_KEY");

        primaryStage.setTitle("AliExpress API Client");

        TabPane tabPane = new TabPane();

        Tab tokenTab = new Tab("Generate Token");
        tokenTab.setContent(createTokenTab());

        Tab apiTab = new Tab("Execute API");
        apiTab.setContent(createApiTab());

        tabPane.getTabs().addAll(tokenTab, apiTab);

        VBox vbox = new VBox(tabPane);
        Scene scene = new Scene(vbox, 1024, 768); // Increased size for better layout
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createTokenTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label authUrlLabel = new Label("Authorization URL:");
        GridPane.setConstraints(authUrlLabel, 0, 0);
        TextField authUrlText = new TextField();
        authUrlText.setEditable(false);
        authUrlText.setText(AliExpressAuthService.generateAuthorizationUrl(appKey));
        GridPane.setConstraints(authUrlText, 1, 0);

        Label redirectedUrlLabel = new Label("Redirected URL:");
        GridPane.setConstraints(redirectedUrlLabel, 0, 1);
        TextField redirectedUrlText = new TextField();
        GridPane.setConstraints(redirectedUrlText, 1, 1);

        Button generateTokenButton = new Button("Generate Token");
        GridPane.setConstraints(generateTokenButton, 1, 2);
        generateTokenButton.setOnAction(e -> {
            String redirectedUrl = redirectedUrlText.getText();
            try {
                AliExpressAuthService.generateNewToken(redirectedUrl);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Token generated successfully!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate token: " + ex.getMessage());
            }
        });

        grid.getChildren().addAll(authUrlLabel, authUrlText, redirectedUrlLabel, redirectedUrlText, generateTokenButton);
        return grid;
    }

    private SplitPane createApiTab() {
        // --- Left Side (Controls) --- //
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        // API Command Selection
        HBox commandSelectionBox = new HBox(10);
        ComboBox<ApiCommand> commandComboBox = new ComboBox<>();
        commandComboBox.setItems(FXCollections.observableList(apiCommandRepository.getCommands()));
        commandComboBox.setConverter(new StringConverter<ApiCommand>() {
            @Override
            public String toString(ApiCommand command) {
                return command == null ? "" : command.getName();
            }

            @Override
            public ApiCommand fromString(String string) {
                return apiCommandRepository.findCommandByName(string);
            }
        });
        commandComboBox.setPromptText("בחר פקודת API");
        
        Label descriptionLabel = new Label("תיאור הפקודה יופיע כאן");
        commandSelectionBox.getChildren().addAll(commandComboBox, descriptionLabel);

        // Pane to hold dynamic parameters
        GridPane parametersGrid = new GridPane();
        parametersGrid.setHgap(10);
        parametersGrid.setVgap(8);
        
        // Wrap parameters grid in a ScrollPane
        ScrollPane parametersScrollPane = new ScrollPane(parametersGrid);
        parametersScrollPane.setFitToWidth(true);
        VBox.setVgrow(parametersScrollPane, Priority.ALWAYS); // Allow scroll pane to grow

        // --- Right Side (Results) --- //
        VBox rightPane = new VBox(5);
        rightPane.setPadding(new Insets(10));
        Label resultLabel = new Label("API Response:");
        TextArea resultArea = new TextArea();
        resultArea.setPromptText("API response will be shown here...");
        resultArea.setEditable(false);
        VBox.setVgrow(resultArea, Priority.ALWAYS);
        rightPane.getChildren().addAll(resultLabel, resultArea);


        // Listener to update parameters and description
        commandComboBox.valueProperty().addListener((obs, oldCommand, newCommand) -> {
            parametersGrid.getChildren().clear();
            parameterTextFields.clear();
            resultArea.clear();
            if (newCommand != null) {
                descriptionLabel.setText(newCommand.getDescription());
                List<ApiParameter> params = newCommand.getParameters();
                for (int i = 0; i < params.size(); i++) {
                    ApiParameter param = params.get(i);
                    // Display name and Hebrew description
                    Label paramLabel = new Label(param.getName() + " (" + param.getDescription() + ")");
                    paramLabel.setTooltip(new Tooltip(param.getDescription()));
                    TextField paramField = new TextField(param.getDefaultValue());
                    parameterTextFields.put(param.getName(), paramField);

                    parametersGrid.add(paramLabel, 0, i);
                    parametersGrid.add(paramField, 1, i);
                }
            } else {
                descriptionLabel.setText("תיאור הפקודה יופיע כאן");
            }
        });

        // Execute Button
        Button executeButton = new Button("Execute");
        executeButton.setOnAction(e -> {
            ApiCommand selectedCommand = commandComboBox.getValue();
            if (selectedCommand == null) {
                showAlert(Alert.AlertType.WARNING, "No command selected", "Please select an API command.");
                return;
            }

            List<String> paramsList = new ArrayList<>();
            for (ApiParameter apiParam : selectedCommand.getParameters()) {
                String paramName = apiParam.getName();
                TextField paramField = parameterTextFields.get(paramName);
                String paramValue = paramField.getText();

                if (paramValue != null && !paramValue.trim().isEmpty()) {
                    paramsList.add(paramName);
                    paramsList.add(paramValue);
                }
            }

            try {
                String result = aliExpressService.execute(selectedCommand.getName(), paramsList);
                resultArea.setText(formatJson(result)); // Pretty print the result
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Execution Error", "Failed to execute API command: " + ex.getMessage());
            }
        });
        
        leftPane.getChildren().addAll(commandSelectionBox, new Separator(), parametersScrollPane, new Separator(), executeButton);

        // --- Main Layout (SplitPane) --- //
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.5); // Initial 50/50 split

        return splitPane;
    }

    /**
     * Formats a JSON string to be more readable (pretty-print).
     * @param jsonString The raw JSON string.
     * @return A formatted JSON string.
     */
    private String formatJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return "";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object jsonObject = mapper.readValue(jsonString, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            // If it's not a valid JSON, return the original string
            return jsonString;
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

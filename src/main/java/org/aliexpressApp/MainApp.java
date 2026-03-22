package org.aliexpressApp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    private final AliExpressAuthService authService = new AliExpressAuthService();
    private final AliExpressService aliExpressService = new AliExpressService();
    private final ApiCommandRepository apiCommandRepository = new ApiCommandRepository();

    // To hold the dynamically created text fields for parameters
    private final Map<String, TextField> parameterTextFields = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AliExpress API Client");

        TabPane tabPane = new TabPane();

        Tab tokenTab = new Tab("Generate Token");
        tokenTab.setContent(createTokenTab());

        Tab apiTab = new Tab("Execute API");
        apiTab.setContent(createApiTab());

        tabPane.getTabs().addAll(tokenTab, apiTab);

        VBox vbox = new VBox(tabPane);
        Scene scene = new Scene(vbox, 800, 600);
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
        authUrlText.setText(authService.getAuthorizationUrl());
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
                authService.generateNewToken(redirectedUrl);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Token generated successfully!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate token: " + ex.getMessage());
            }
        });

        grid.getChildren().addAll(authUrlLabel, authUrlText, redirectedUrlLabel, redirectedUrlText, generateTokenButton);
        return grid;
    }

    private VBox createApiTab() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        // --- API Command Selection --- //
        HBox commandSelectionBox = new HBox(10);
        
        ComboBox<ApiCommand> commandComboBox = new ComboBox<>();
        commandComboBox.setItems(FXCollections.observableList(apiCommandRepository.getCommands()));
        commandComboBox.setConverter(new StringConverter<ApiCommand>() {
            @Override
            public String toString(ApiCommand command) {
                // Display the actual command name in the ComboBox
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
        parametersGrid.setVgap(5);

        // Results Area
        TextArea resultArea = new TextArea();
        resultArea.setPromptText("API response will be shown here...");
        resultArea.setEditable(false);

        // Listener to update parameters and description when a new command is selected
        commandComboBox.valueProperty().addListener((obs, oldCommand, newCommand) -> {
            parametersGrid.getChildren().clear();
            parameterTextFields.clear();
            if (newCommand != null) {
                descriptionLabel.setText(newCommand.getDescription());
                List<ApiParameter> params = newCommand.getParameters();
                for (int i = 0; i < params.size(); i++) {
                    ApiParameter param = params.get(i);
                    Label paramLabel = new Label(param.getName());
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

                if (paramValue != null && !paramValue.isEmpty()) {
                    paramsList.add(paramName);
                    paramsList.add(paramValue);
                }
            }

            try {
                String result = aliExpressService.execute(selectedCommand.getName(), paramsList);
                resultArea.setText(result);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Execution Error", "Failed to execute API command: " + ex.getMessage());
            }
        });

        container.getChildren().addAll(commandSelectionBox, new Separator(), parametersGrid, new Separator(), executeButton, resultArea);
        return container;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

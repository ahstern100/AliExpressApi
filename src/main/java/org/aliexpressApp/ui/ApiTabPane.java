package org.aliexpressApp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.aliexpressApp.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiTabPane extends SplitPane {

    private final ApiCommandRepository apiCommandRepository;
    private final AliExpressService aliExpressService;
    private final ObjectMapper objectMapper;

    private final Map<String, Node> parameterInputNodes = new HashMap<>();

    public ApiTabPane(ApiCommandRepository apiCommandRepository, AliExpressService aliExpressService, ObjectMapper objectMapper) {
        this.apiCommandRepository = apiCommandRepository;
        this.aliExpressService = aliExpressService;
        this.objectMapper = objectMapper;
        buildUI();
    }

    private void buildUI() {
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

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

        GridPane parametersGrid = new GridPane();
        parametersGrid.setHgap(10);
        parametersGrid.setVgap(8);

        ScrollPane parametersScrollPane = new ScrollPane(parametersGrid);
        parametersScrollPane.setFitToWidth(true);
        VBox.setVgrow(parametersScrollPane, Priority.ALWAYS);

        VBox rightPane = new VBox(5);
        rightPane.setPadding(new Insets(10));
        Label resultLabel = new Label("API Response:");
        TextArea resultArea = new TextArea();
        resultArea.setPromptText("API response will be shown here...");
        resultArea.setEditable(false);
        VBox.setVgrow(resultArea, Priority.ALWAYS);
        rightPane.getChildren().addAll(resultLabel, resultArea);

        commandComboBox.valueProperty().addListener((obs, oldCommand, newCommand) -> {
            parametersGrid.getChildren().clear();
            parameterInputNodes.clear();
            resultArea.clear();
            if (newCommand != null) {
                descriptionLabel.setText(newCommand.getDescription());
                buildParametersUI(parametersGrid, newCommand.getParameters(), "");
            } else {
                descriptionLabel.setText("תיאור הפקודה יופיע כאן");
            }
        });

        Button executeButton = new Button("Execute");
        executeButton.setOnAction(e -> {
            ApiCommand selectedCommand = commandComboBox.getValue();
            if (selectedCommand == null) {
                UIUtils.showAlert(Alert.AlertType.WARNING, "No command selected", "Please select an API command.");
                return;
            }

            try {
                List<String> paramsList = buildParamList(selectedCommand.getParameters());
                String result = aliExpressService.execute(selectedCommand.getName(), paramsList);
                resultArea.setText(UIUtils.formatJson(result, objectMapper));
            } catch (Exception ex) {
                UIUtils.showAlert(Alert.AlertType.ERROR, "Execution Error", "Failed to execute API command: " + ex.getMessage());
                ex.printStackTrace(); // For debugging
            }
        });

        leftPane.getChildren().addAll(commandSelectionBox, new Separator(), parametersScrollPane, new Separator(), executeButton);

        getItems().addAll(leftPane, rightPane);
        setDividerPositions(0.5);
    }

    private void buildParametersUI(GridPane grid, List<ApiParameter> parameters, String parentPrefix) {
        int rowIndex = 0;
        for (ApiParameter param : parameters) {
            String currentPath = parentPrefix.isEmpty() ? param.getName() : parentPrefix + "." + param.getName();
            Label paramLabel = new Label(param.getName() + " (" + param.getDescription() + ")");

            if (param.isComplex()) {
                GridPane innerGrid = new GridPane();
                innerGrid.setHgap(10);
                innerGrid.setVgap(5);
                innerGrid.setPadding(new Insets(10));

                TitledPane titledPane = new TitledPane(param.getDescription(), innerGrid);
                titledPane.setCollapsible(true);
                titledPane.setExpanded(false);
                grid.add(titledPane, 0, rowIndex++, 2, 1);

                Field[] fields = param.getType().getDeclaredFields();
                List<ApiParameter> innerParams = new ArrayList<>();
                for (Field field : fields) {
                    innerParams.add(new ApiParameter(field.getName(), field.getName(), "", field.getType(), false));
                }
                buildParametersUI(innerGrid, innerParams, currentPath);

            } else {
                TextField paramField = new TextField(param.getDefaultValue());
                parameterInputNodes.put(currentPath, paramField);
                grid.add(paramLabel, 0, rowIndex);
                grid.add(paramField, 1, rowIndex++);
            }
        }
    }

    private List<String> buildParamList(List<ApiParameter> parameters) throws Exception {
        List<String> paramsList = new ArrayList<>();
        Map<String, Object> complexParams = new HashMap<>();

        for (ApiParameter apiParam : parameters) {
            if (apiParam.isComplex()) {
                Object dtoInstance = createDtoInstance(apiParam.getType(), apiParam.getName());
                complexParams.put(apiParam.getName(), dtoInstance);
            } else {
                String paramName = apiParam.getName();
                Node inputNode = parameterInputNodes.get(paramName);
                if (inputNode instanceof TextField) {
                    String paramValue = ((TextField) inputNode).getText();
                    if (paramValue != null && !paramValue.trim().isEmpty()) {
                        paramsList.add(paramName);
                        paramsList.add(paramValue);
                    }
                }
            }
        }

        for (Map.Entry<String, Object> entry : complexParams.entrySet()) {
            paramsList.add(entry.getKey());
            paramsList.add(objectMapper.writeValueAsString(entry.getValue()));
        }

        return paramsList;
    }

    private Object createDtoInstance(Class<?> dtoClass, String basePath) throws Exception {
        Object instance = dtoClass.getDeclaredConstructor().newInstance();
        for (Field field : dtoClass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldPath = basePath + "." + field.getName();
            Node inputNode = parameterInputNodes.get(fieldPath);
            if (inputNode instanceof TextField) {
                String value = ((TextField) inputNode).getText();
                if (value != null && !value.trim().isEmpty()) {
                    Object convertedValue = convertString(value, field.getType());
                    field.set(instance, convertedValue);
                }
            }
        }
        return instance;
    }

    private Object convertString(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        return value; // Fallback
    }
}

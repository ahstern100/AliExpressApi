package org.aliexpressApp.ui;

import org.aliexpressApp.AliExpressAuthService;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class TokenTabPane extends GridPane {

    private final String appKey;

    public TokenTabPane(String appKey) {
        this.appKey = appKey;
        buildUI();
    }

    private void buildUI() {
        setPadding(new Insets(10, 10, 10, 10));
        setVgap(8);
        setHgap(10);

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
                UIUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Token generated successfully!");
            } catch (Exception ex) {
                UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate token: " + ex.getMessage());
            }
        });

        getChildren().addAll(authUrlLabel, authUrlText, redirectedUrlLabel, redirectedUrlText, generateTokenButton);
    }
}

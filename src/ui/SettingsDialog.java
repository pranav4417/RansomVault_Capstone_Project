package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainsys.VaultFormatter;

import java.io.File;

public class SettingsDialog {

    private final Stage parentStage;
    private String vaultPath;

    public SettingsDialog(Stage parentStage, String vaultPath){
        this.parentStage = parentStage;
        this.vaultPath = vaultPath;
    }

    public void show(){
        Stage stage = new Stage();
        stage.setTitle("Settings");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        // Vault selection
        Button selectVaultBtn = new Button("Select VeraCrypt Vault (.vc)");
        Label vaultLabel = new Label("Current Vault: " + vaultPath);
        selectVaultBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Vault File");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("VeraCrypt Files","*.vc"));
            File selected = chooser.showOpenDialog(stage);
            if(selected!=null){
                vaultPath = selected.getAbsolutePath();
                vaultLabel.setText("Current Vault: " + vaultPath);
            }
        });

        // Change PIN
        Button changePinBtn = new Button("Change PIN");
        changePinBtn.setOnAction(e -> {
            TextInputDialog firstDialog = new TextInputDialog();
            firstDialog.setHeaderText("Enter new 6-digit PIN:");
            firstDialog.showAndWait().ifPresent(firstPin -> {
                if (firstPin.length() != 6 || !firstPin.matches("\\d{6}")) {
                    new Alert(Alert.AlertType.ERROR, "PIN must be exactly 6 digits!").show();
                    return;
                }
                // Confirm entry
                TextInputDialog confirmDialog = new TextInputDialog();
                confirmDialog.setHeaderText("Re-enter new PIN for confirmation:");
                confirmDialog.showAndWait().ifPresent(confirmPin -> {
                    if (!firstPin.equals(confirmPin)) {
                        new Alert(Alert.AlertType.ERROR, "PINs do not match!").show();
                        return;
                    }
                    // Save PIN to MainUI
                    MainUI.setAppPin(firstPin);
                    new Alert(Alert.AlertType.INFORMATION, "✅ PIN successfully changed!").show();
                });
            });
        });

        // Theme selection
        ChoiceBox<String> themeChoice = new ChoiceBox<>();
        themeChoice.getItems().addAll("Light","Dark");
        themeChoice.setValue("Dark");

        // Format Vault
        Button formatVaultBtn = new Button("Format Vault");
        formatVaultBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "⚠ All data in the vault will be lost! Continue?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(bt -> {
                if(bt == ButtonType.YES){
                    VaultFormatter.formatVault(vaultPath); // asks password internally
                }
            });
        });

        // Create New Vault
        Button createVaultBtn = new Button("Create New Vault");
        createVaultBtn.setOnAction(e -> {
            TextInputDialog sizeDialog = new TextInputDialog("1");
            sizeDialog.setHeaderText("Enter Vault Size (in GB):");
            sizeDialog.showAndWait().ifPresent(sizeStr -> {
                try {
                    int sizeGB = Integer.parseInt(sizeStr);
                    TextInputDialog pwdDialog = new TextInputDialog();
                    pwdDialog.setHeaderText("Enter Vault Password:");
                    pwdDialog.showAndWait().ifPresent(password -> {
                        VaultFormatter.createVault(vaultPath, sizeGB, password);
                    });
                } catch (NumberFormatException ex) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Invalid size entered!");
                    a.show();
                }
            });
        });

        // Placeholder for VeraCrypt installer (Windows/Mac)
        Button installVCBtn = new Button("Install VeraCrypt");
        installVCBtn.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "VeraCrypt installer functionality will be implemented here.");
            a.show();
        });

        // Security questions placeholder
        Button securityQABtn = new Button("Security Questions");
        securityQABtn.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "Security questions management will be implemented here.");
            a.show();
        });

        root.getChildren().addAll(
                selectVaultBtn, vaultLabel,
                changePinBtn, new Label("Theme:"), themeChoice,
                formatVaultBtn, createVaultBtn,
                installVCBtn, securityQABtn
        );

        Scene scene = new Scene(root,400,400);
        stage.setScene(scene);
        stage.show();
    }
}

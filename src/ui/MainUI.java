package ui;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mainsys.BackupEngine;
import mainsys.VaultManager;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Paths;
import java.util.Base64;

public class MainUI extends Application {

    private static final String PIN_FILE = "pin.dat";
    private static final String SECRET_KEY = "MySecretAESKey12"; // 16 chars = 128-bit key
    private static String APP_PIN = "123456"; // default

    // Load PIN from file
    private static void loadPin() {
        File f = new File(PIN_FILE);
        if (!f.exists()) {
            savePin(APP_PIN); // save default
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String encrypted = br.readLine();
            if (encrypted != null && !encrypted.isEmpty()) {
                APP_PIN = decrypt(encrypted);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Save PIN to file
    private static void savePin(String pin) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PIN_FILE))) {
            bw.write(encrypt(pin));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void setAppPin(String newPin){
        APP_PIN = newPin;
        savePin(newPin); // persist
    }

    // AES encrypt
    private static String encrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes()));
    }

    // AES decrypt
    private static String decrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(str)));
    }

    // ----------- Main UI code -----------------
    private String folderToBackup = null;
    private String vaultPath = "/Users/pranav/Documents/CapstoneProj/Vault/RansomSafe.vc";
    private String vaultPassword = "pranav123";
    private String hashStoreFile = "/Users/pranav/Documents/hash_store.txt";
    private final String mountPoint = "/Volumes/Safe";

    private BackupEngine backupEngine;
    private TextField folderPathField;
    private Label statusLabel;
    private ProgressBar progressBar;

    @Override
    public void start(Stage stage) {
        // ====== Set icon safely ======
        InputStream iconStream = getClass().getResourceAsStream("/icon.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        }

        loadPin(); // load persistent PIN
        showPinLock(stage);
    }

    // ---------------- PIN SCREEN -----------------
    private void showPinLock(Stage stage) {
        VBox wrapper = new VBox(25);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(40));

        BackgroundImage bg = new BackgroundImage(
                new Image(MainUI.class.getResource("/resources/bg.jpg").toExternalForm(),
                        420, 550, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        wrapper.setBackground(new Background(bg));

        Label lbl = new Label("🔒 RansomShield");
        lbl.setFont(Font.font("Arial Black", 28));
        lbl.setTextFill(Color.web("#00d4ff"));

        Label subLbl = new Label("Enter Your 6-digit PIN");
        subLbl.setFont(Font.font("Arial", 16));
        subLbl.setTextFill(Color.WHITE);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("******");
        pinField.setEditable(false);
        pinField.setMaxWidth(220);
        pinField.setStyle("-fx-font-size: 18px; -fx-text-fill: cyan; "
                + "-fx-background-color: rgba(30,30,30,0.7); "
                + "-fx-background-radius: 15; "
                + "-fx-border-color: #00d4ff; -fx-border-radius: 15; -fx-border-width: 2;");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        GridPane keypad = new GridPane();
        keypad.setHgap(12);
        keypad.setVgap(12);
        keypad.setAlignment(Pos.CENTER);

        String[] keys = {"1","2","3","4","5","6","7","8","9","0"};
        int row = 0, col = 0;
        for (String key : keys){
            Button btn = styledButton(key, "#2c2c2c");
            btn.setOnAction(e -> { if (pinField.getText().length() < 6) pinField.setText(pinField.getText() + key); });
            keypad.add(btn, col, row);
            col++; if (col==3){col=0; row++;}
        }

        Button backspaceBtn = styledButton("⌫", "#900");
        backspaceBtn.setOnAction(e -> {
            String text = pinField.getText();
            if (!text.isEmpty()) pinField.setText(text.substring(0,text.length()-1));
        });
        keypad.add(backspaceBtn,2,3);

        Button unlockBtn = styledButton("Unlock", "#00d4ff");
        unlockBtn.setStyle(unlockBtn.getStyle() + "-fx-text-fill:black; -fx-font-weight:bold;");
        unlockBtn.setPrefWidth(220);
        unlockBtn.setOnAction(e -> {
            if(pinField.getText().equals(APP_PIN)) loadDashboard(stage);
            else { errorLabel.setText("❌ Invalid PIN"); pinField.clear(); }
        });

        wrapper.getChildren().addAll(lbl, subLbl, pinField, keypad, unlockBtn, errorLabel);

        Scene scene = new Scene(wrapper, 420, 550);
        stage.setScene(scene);
        stage.setTitle("RansomShield - PIN Lock");
        stage.show();
    }

    // ---------------- DASHBOARD -----------------
    private void loadDashboard(Stage stage){
        TabPane tabPane = new TabPane();

        BackgroundImage bg = new BackgroundImage(
                new Image(MainUI.class.getResource("/resources/bg.jpg").toExternalForm(),
                        850, 550, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        Tab dashboardTab = new Tab("📊 Dashboard");
        dashboardTab.setClosable(false);

        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(25));
        dashboard.setAlignment(Pos.TOP_CENTER);
        dashboard.setBackground(new Background(bg));

        Label title = new Label("RansomShield Dashboard");
        title.setFont(Font.font("Arial Black", 22));
        title.setTextFill(Color.web("#00d4ff"));

        folderPathField = new TextField();
        folderPathField.setPromptText("Select folder to backup");
        folderPathField.setEditable(false);
        folderPathField.setPrefWidth(400);
        folderPathField.setStyle("-fx-background-color: rgba(44,44,44,0.7); "
                + "-fx-text-fill: white; -fx-prompt-text-fill: #bbb; "
                + "-fx-font-size: 14px; -fx-background-radius: 15;");

        Button selectFolderBtn = styledButton("📂 Select Folder", "#444");
        selectFolderBtn.setOnAction(e -> selectFolder(stage));

        HBox folderBox = new HBox(15, folderPathField, selectFolderBtn);
        folderBox.setAlignment(Pos.CENTER);

        Button backupBtn = styledButton("💾 Backup Now", "#00d4ff");
        backupBtn.setStyle(backupBtn.getStyle() + "-fx-text-fill:black;");
        Button mountBtn = styledButton("🔓 Mount Vault", "#2ecc71");
        Button lockBtn = styledButton("🔒 Lock Vault", "#e74c3c");

        backupBtn.setOnAction(e -> runBackup());
        mountBtn.setOnAction(e -> {
            try { VaultManager.mountVault(vaultPath, vaultPassword); setStatus("✅ Vault mounted"); }
            catch(Exception ex) { setStatus("❌ Vault mount failed: "+ex.getMessage()); }
        });
        lockBtn.setOnAction(e -> {
            try { VaultManager.dismountVault(); setStatus("✅ Vault locked"); }
            catch(Exception ex) { setStatus("❌ Vault lock failed: "+ex.getMessage()); }
        });

        HBox actionButtons = new HBox(20, backupBtn, mountBtn, lockBtn);
        actionButtons.setAlignment(Pos.CENTER);

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(350);
        progressBar.setVisible(false);

        statusLabel = new Label("Status: Idle");
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setTextFill(Color.WHITE);

        dashboard.getChildren().addAll(title, folderBox, actionButtons, progressBar, statusLabel);
        dashboardTab.setContent(dashboard);

        Tab settingsTab = new Tab("⚙ Settings");
        settingsTab.setClosable(false);
        VBox settingsBox = new VBox(20);
        settingsBox.setPadding(new Insets(25));
        settingsBox.setAlignment(Pos.TOP_LEFT);
        settingsBox.setBackground(new Background(bg));

        Button settingsBtn = styledButton("Open Settings Dialog", "#444");
        settingsBtn.setOnAction(e -> new SettingsDialog(stage, vaultPath).show());
        settingsBox.getChildren().add(settingsBtn);
        settingsTab.setContent(settingsBox);

        tabPane.getTabs().addAll(dashboardTab, settingsTab);

        Scene scene = new Scene(tabPane, 850, 550);
        stage.setScene(scene);
        stage.setTitle("RansomShield - Dashboard");
        stage.show();
    }

    private Button styledButton(String text, String color){
        Button btn = new Button(text);
        btn.setPrefHeight(45);
        btn.setPrefWidth(150);
        btn.setStyle("-fx-font-size: 14px; "
                + "-fx-background-color:" + color + "; "
                + "-fx-background-radius: 20; "
                + "-fx-text-fill:white; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10,0,0,4);");
        return btn;
    }

    private void selectFolder(Stage stage){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder to Backup");
        File selected = chooser.showDialog(stage);
        if(selected!=null){
            folderToBackup = selected.getAbsolutePath();
            folderPathField.setText(folderToBackup);
            setStatus("📂 Selected: "+folderToBackup);
        }
    }

    private void runBackup(){
        if(folderToBackup==null){ setStatus("⚠ Please select a folder!"); return; }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("🔐 Mounting vault...");
                VaultManager.mountVault(vaultPath, vaultPassword);

                updateMessage("🔧 Preparing backup engine...");
                backupEngine = new BackupEngine(Paths.get("/Volumes/Safe"), Paths.get(hashStoreFile));

                updateMessage("📂 Backing up files...");
                backupEngine.backupFolder(Paths.get(folderToBackup), Paths.get("/Volumes/Safe"));

                updateMessage("🔒 Dismounting vault...");
                VaultManager.dismountVault();

                updateMessage("✅ Backup completed and vault locked.");
                return null;
            }
        };
        task.messageProperty().addListener((obs,o,n)-> setStatus(n));
        task.setOnRunning(e-> { progressBar.setVisible(true); progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS); });
        task.setOnSucceeded(e-> progressBar.setVisible(false));
        task.setOnFailed(e-> { progressBar.setVisible(false); setStatus("❌ Backup failed"); task.getException().printStackTrace(); });
        new Thread(task,"backup-thread").start();
    }

    private void setStatus(String msg){ statusLabel.setText("Status: "+msg); }

    public static void main(String[] args){ launch(args); }
}
package mainsys;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("🔐 RansomShieldJava is launching...");

            // Load configuration from config.json
            Config config = Config.load("config.json");

            // Convert string paths to Path objects
            Path vaultPath = Paths.get(config.vaultPath);
            Path folderToBackup = Paths.get(config.folderToBackup);

            // Create backup engine
            BackupEngine engine = new BackupEngine(vaultPath, config.vaultPassword, folderToBackup);

            // Start backup
            engine.backupFolder(folderToBackup, vaultPath);

            System.out.println("✅ Backup completed successfully.");

        } catch (Exception e) {
            System.out.println("❌ Error during backup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

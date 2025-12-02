package mainsys;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VaultFormatter {

    // Path to VeraCrypt executable
    private static final String VC_PATH_MAC = "/usr/local/bin/veracrypt";
    private static final String VC_PATH_WIN = "C:\\Program Files\\VeraCrypt\\VeraCrypt.exe";

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static String getVCPath() {
        return isWindows() ? VC_PATH_WIN : VC_PATH_MAC;
    }

    // Run command helper
    private static void runCommand(ProcessBuilder pb) throws Exception {
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("[VaultFormatter] " + line);
        }
        int exitCode = p.waitFor();
        if (exitCode != 0) throw new RuntimeException("Command failed with exit code: " + exitCode);
    }

    // Format existing vault
    public static void formatVault(String vaultPath, String password) {
        try {
            String sizeArg = "1G"; // for formatting, size is ignored
            String filesystem = "exFAT"; // always exFAT for mac/win
            String randomSource = isWindows() ? "" : "/dev/urandom";

            ProcessBuilder pb = new ProcessBuilder(
                    getVCPath(),
                    "--text",
                    "--non-interactive",
                    "--create", vaultPath,
                    "--size", sizeArg,
                    "--encryption", "AES",
                    "--hash", "SHA-512",
                    "--filesystem", filesystem,
                    "--volume-type", "normal",
                    "--pim", "0",
                    "--password", password,
                    "--random-source", randomSource
            );

            runCommand(pb);
            System.out.println("[VaultFormatter] Vault formatted successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Overload: ask user for password
    public static void formatVault(String vaultPath) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setHeaderText("Enter new password for vault");
        dialog.showAndWait().ifPresent(password -> formatVault(vaultPath, password));
    }

    // Create new vault with size in GB
    public static void createVault(String vaultPath, int sizeGB, String password) {
        try {
            String sizeArg = sizeGB + "G";
            String filesystem = "exFAT";
            String randomSource = isWindows() ? "" : "/dev/urandom";

            ProcessBuilder pb = new ProcessBuilder(
                    getVCPath(),
                    "--text",
                    "--non-interactive",
                    "--create", vaultPath,
                    "--size", sizeArg,
                    "--encryption", "AES",
                    "--hash", "SHA-512",
                    "--filesystem", filesystem,
                    "--volume-type", "normal",
                    "--pim", "0",
                    "--password", password,
                    "--random-source", randomSource
            );

            runCommand(pb);
            System.out.println("[VaultFormatter] Vault created successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

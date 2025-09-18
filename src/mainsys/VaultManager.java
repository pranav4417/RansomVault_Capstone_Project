package mainsys;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VaultManager {

    private static final String VC_PATH = "/usr/local/bin/veracrypt"; // adjust if VeraCrypt installed elsewhere
    private static final String MOUNT_POINT = "/Volumes/Safe";

    // Mount vault
    public static void mountVault(String vaultPath, String password) throws Exception {
        if (isMounted()) {
            System.out.println("[INFO] Vault already mounted at " + MOUNT_POINT);
            return;
        }
        ProcessBuilder pb = new ProcessBuilder(
            VC_PATH, "--text", "--non-interactive",
            "--mount", vaultPath, MOUNT_POINT,
            "--password=" + password
        );
        runCommand(pb);
    }

    // Dismount vault
    public static void dismountVault() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            VC_PATH, "--text", "--non-interactive",
            "--dismount", MOUNT_POINT
        );
        runCommand(pb);
    }

    // Check if mounted
    public static boolean isMounted() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(VC_PATH, "--text", "--list");
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(MOUNT_POINT)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Run command helper
    private static void runCommand(ProcessBuilder pb) throws Exception {
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[VeraCrypt] " + line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("VeraCrypt failed with exit code: " + exitCode);
        }
    }
}

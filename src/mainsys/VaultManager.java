package mainsys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

public class VaultManager {

    private static final String VC_PATH_MAC = "/usr/local/bin/veracrypt";
    private static final String VC_PATH_WIN = "C:\\Program Files\\VeraCrypt\\VeraCrypt.exe";
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    private static String getVCPath() {
        return isWindows() ? VC_PATH_WIN : VC_PATH_MAC;
    }

    // Helper to run command
    private static void runCommand(String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("[VaultManager] " + line);
        }
        int exitCode = p.waitFor();
        if (exitCode != 0) throw new RuntimeException("[VaultManager] VeraCrypt command failed with exit code: " + exitCode);
    }

    // Mount vault to specific mount point
    public static void mountVault(String vaultPath, String password, String mountPoint) {
        try {
            File mount = new File(mountPoint);
            if (!isWindows() && mount.exists()) mount.delete(); // clean mount point on mac

            String cmd = String.format("%s --text --non-interactive --mount %s %s --password=%s",
                    getVCPath(), vaultPath, mountPoint, password);

            if (isWindows()) cmd = String.format("\"%s\" --text --non-interactive --mount \"%s\" \"%s\" --password=\"%s\"",
                    getVCPath(), vaultPath, mountPoint, password);

            runCommand(cmd.split(" "));
            System.out.println("[VaultManager] Vault mounted successfully at " + mountPoint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Overload: default mount point
    public static void mountVault(String vaultPath, String password) {
        String mountPoint = isWindows() ? "X:" : "/Volumes/Safe";
        mountVault(vaultPath, password, mountPoint);
    }

    // Dismount vault
    public static void dismountVault(String mountPoint) {
        try {
            String cmd = String.format("%s --text --non-interactive --dismount %s", getVCPath(), mountPoint);
            if (isWindows()) cmd = String.format("\"%s\" --text --non-interactive --dismount \"%s\"", getVCPath(), mountPoint);
            runCommand(cmd.split(" "));
            System.out.println("[VaultManager] Vault dismounted successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Overload: default mount point
    public static void dismountVault() {
        String mountPoint = isWindows() ? "X:" : "/Volumes/Safe";
        dismountVault(mountPoint);
    }
}

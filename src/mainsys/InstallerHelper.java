package mainsys;

import java.io.File;
import java.io.IOException;

public class InstallerHelper {

    public static void installVeraCryptAndFuse() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if(os.contains("win")) {
                // Assuming installer is bundled in resources
                File vcInstaller = new File("installers/VeraCryptSetup.exe");
                if(vcInstaller.exists()) {
                    new ProcessBuilder(vcInstaller.getAbsolutePath()).start();
                    System.out.println("[Installer] VeraCrypt installer launched for Windows.");
                }
            } else if(os.contains("mac")) {
                File vcInstaller = new File("installers/VeraCrypt.dmg");
                File fuseInstaller = new File("installers/macfuse-5.0.6.dmg");
                if(vcInstaller.exists() && fuseInstaller.exists()) {
                    new ProcessBuilder("hdiutil","attach", fuseInstaller.getAbsolutePath()).start();
                    new ProcessBuilder("hdiutil","attach", vcInstaller.getAbsolutePath()).start();
                    System.out.println("[Installer] VeraCrypt and macFUSE installer launched for macOS.");
                }
            } else {
                System.out.println("[Installer] OS not supported for embedded installer.");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

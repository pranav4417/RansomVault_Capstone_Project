package mainsys;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class AutoBackupScheduler {
    private Timer timer = new Timer();
    private FileHashManager fileHashManager = new FileHashManager();

    public void startAutoBackup(File folderToWatch) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (folderToWatch.exists() && folderToWatch.isDirectory()) {
                    for (File f : folderToWatch.listFiles()) {
                        if (f.isFile() && fileHashManager.hasFileChanged(f)) {
                            VaultLogger.log("Auto backup triggered for: " + f.getName());
                            // here you can call your BackupEngine to back it up
                        }
                    }
                }
            }
        }, 0, 300000); // every 5 min (300000 ms)
    }

    public void stopAutoBackup() {
        timer.cancel();
    }
}

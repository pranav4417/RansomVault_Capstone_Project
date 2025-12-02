package mainsys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class FileHashManager {
    private Map<String, String> hashStore = new HashMap<>();

    // Generate SHA-256 hash for a file
    private String generateHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();

        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // Check if file has changed since last hash
    public boolean hasFileChanged(File file) {
        try {
            String newHash = generateHash(file);
            String oldHash = hashStore.get(file.getAbsolutePath());

            if (oldHash == null) {
                hashStore.put(file.getAbsolutePath(), newHash);
                return true; // new file
            }

            if (!newHash.equals(oldHash)) {
                hashStore.put(file.getAbsolutePath(), newHash);
                return true; // file changed
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

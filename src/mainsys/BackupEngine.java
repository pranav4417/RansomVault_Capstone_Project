package mainsys;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

public class BackupEngine {
    private final Path vaultPath;
    private final Path hashStoreFile;
    private final Map<String, String> hashMap = new HashMap<>();
    private static final int MAX_VERSIONS = 2; // Keep last 2 versions

    public BackupEngine(Path vaultPath, Path hashStoreFile) {
        this.vaultPath = vaultPath;
        this.hashStoreFile = hashStoreFile;
        loadHashes();
    }

    public void backupFolder(Path sourceFolder, Path targetFolder) throws Exception {
        Files.walk(sourceFolder).forEach(source -> {
            try {
                if (!Files.isRegularFile(source)) return;

                String fileName = source.getFileName().toString();
                
                // Skip macOS hidden/system files
                if (fileName.equals(".DS_Store") || fileName.startsWith("._") || fileName.startsWith(".")) return;

                Path relative = sourceFolder.relativize(source);
                Path targetDir = targetFolder.resolve(relative).getParent();

                String newHash = computeHash(source);
                String oldHash = hashMap.get(source.toString());

                if (oldHash == null || !newHash.equals(oldHash)) {
                    Files.createDirectories(targetDir);

                    // Versioned filename (_1, _2)
                    Path versionedTarget = getVersionedFile(targetDir, fileName);

                    Files.copy(source, versionedTarget, StandardCopyOption.REPLACE_EXISTING);
                    hashMap.put(source.toString(), newHash);

                    System.out.println("[Backup] New version saved: " + versionedTarget.getFileName());

                    // Clean old versions beyond MAX_VERSIONS
                    cleanupOldVersions(targetDir, fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        saveHashes();
    }

    // Generate next versioned file path
    private Path getVersionedFile(Path targetDir, String baseName) {
        File[] existing = targetDir.toFile().listFiles((dir, name) -> name.startsWith(baseName));
        int nextVersion = (existing == null) ? 1 : existing.length + 1;

        String nameWithoutExt = baseName.contains(".") ? baseName.substring(0, baseName.lastIndexOf('.')) : baseName;
        String ext = baseName.contains(".") ? baseName.substring(baseName.lastIndexOf('.')) : "";
        String newName = nameWithoutExt + (nextVersion > 1 ? "(" + nextVersion + ")" : "") + ext;

        return targetDir.resolve(newName);
    }

    // Cleanup older versions beyond MAX_VERSIONS
    private void cleanupOldVersions(Path targetDir, String baseName) {
        try {
            File[] versions = targetDir.toFile().listFiles((dir, name) -> name.startsWith(baseName.split("\\.")[0]));
            if (versions == null || versions.length <= MAX_VERSIONS) return;

            Arrays.sort(versions, Comparator.comparingLong(File::lastModified));

            for (int i = 0; i < versions.length - MAX_VERSIONS; i++) {
                if (versions[i].delete()) {
                    System.out.println("[Cleanup] Old version deleted: " + versions[i].getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHashes() {
        try {
            if (!Files.exists(hashStoreFile)) return;
            Files.lines(hashStoreFile).forEach(line -> {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) hashMap.put(parts[0], parts[1]);
            });
        } catch (IOException e) {
            System.err.println("Could not load hash store: " + e.getMessage());
        }
    }

    private void saveHashes() {
        try {
            Files.createDirectories(hashStoreFile.getParent());
            Files.write(hashStoreFile,
                () -> hashMap.entrySet().stream()
                    .<CharSequence>map(e -> e.getKey() + ":" + e.getValue())
                    .iterator());
        } catch (IOException e) {
            System.err.println("Could not save hash store: " + e.getMessage());
        }
    }

    private String computeHash(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] data = Files.readAllBytes(file);
        byte[] hash = digest.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

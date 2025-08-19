package mainsys;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class HashStore {

    private Map<String, String> hashMap = new HashMap<>(); // ✅ Always initialized

    public void load() throws Exception {
        File file = new File("hash_store.json");
        if (file.exists()) {
            Gson gson = new Gson();
            Map<String, String> loaded = gson.fromJson(new FileReader(file), Map.class);
            if (loaded != null) {
                hashMap = loaded;
            } else {
                hashMap = new HashMap<>();
            }
        } else {
            hashMap = new HashMap<>();
        }
    }

    public void save() throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("hash_store.json")) {
            gson.toJson(hashMap, writer);
        }
    }

    public boolean isChanged(String filePath, String newHash) {
        return !newHash.equals(hashMap.get(filePath));
    }

    public void update(String filePath, String newHash) {
        hashMap.put(filePath, newHash);
    }

    public String getHash(String filePath) {
        return hashMap.get(filePath);
    }
}

package mainsys;

import java.io.FileWriter;
import java.io.IOException;

public class SecurityManager {

    public static void saveSecurityQA(String question, String answer) {
        try (FileWriter fw = new FileWriter("securityQA.txt", false)) {
            fw.write(question + ":" + answer);
            System.out.println("[Security] Security question and answer saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

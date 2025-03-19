package com.notar;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import com.notar.BigBrainCipher.BigBrainCipher;
import com.notar.BigBrainCipher.RSAFactory;

public class App {
    public static void main(String[] args) throws Exception {
        ConsolePrinter consolePrinter = new ConsolePrinter();
        KeyCreator keyCreator = new KeyCreator();

        while (true) {
            try {

                String input = consolePrinter.startProgramm();
                switch (input) {
                    case "1":
                        String path = consolePrinter.creatingKeys();
                        keyCreator.createKeys(path);
                        break;
                    case "2":
                        String[] paths = consolePrinter.recoverFiles();
                        File privateKey = new File(paths[0]);
                        recoverAllFilesInDirectory(readRSAPrivateKey(privateKey), paths[1],
                                paths[2]);
                        break;
                    case "3":
                        System.exit(0);
                    default:
                        break;
                }
            } catch (Exception e) {
                continue;
            }

        }

    }

    public static void recoverAllFilesInDirectory(String privateKey, String filepath, String ablageOrt) {
        File directory = new File(filepath);
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String fileName = child.getName();

                FileEncrypter fE = new FileEncrypter();
                BigBrainCipher cipher = new BigBrainCipher(new RSAFactory());
                try {
                    String notarInfoEncrypted = fE.getNotarEncryptedKey(filepath + fileName);
                    String notarInfoDecrypted = cipher.getFactory().decrypt(privateKey, notarInfoEncrypted.getBytes());
                    String AESKey = notarInfoDecrypted.split(" ")[0];
                    String fileNameDecrypted = notarInfoDecrypted.split(" ")[1];

                    fE.setNotarReadInfo(true);
                    fE.decryptFile(AESKey, filepath + fileName, fileNameDecrypted, ablageOrt);

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }
        }
    }

    private static String readRSAPrivateKey(File file) throws Exception {
        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

        String publicKeyPEM = key
                .replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s", "");

        return publicKeyPEM;
    }
}
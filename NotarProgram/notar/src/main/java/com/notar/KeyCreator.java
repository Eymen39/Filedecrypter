package com.notar;

import java.io.FileWriter;
import java.io.IOException;

import com.notar.BigBrainCipher.BigBrainCipher;
import com.notar.BigBrainCipher.RSAFactory;

public class KeyCreator {

    private static String pemFileBeginning = "-----BEGIN %s KEY-----\n";
    private static String pemFileEnd = "\n-----END %s KEY-----";

    public void createKeys(String outputFile) throws IOException {
        BigBrainCipher bbc = new BigBrainCipher(new RSAFactory());

        String[] keys = (String[]) bbc.getFactory().createKey();

        FileWriter privateKey = new FileWriter(outputFile + "privateKey.pem");
        FileWriter publicKey = new FileWriter(outputFile + "publicKey.pem");
        privateKey.write(generatePemFile(keys[0], "PRIVATE"));
        publicKey.write(generatePemFile(keys[1], "PUBLIC"));

        privateKey.close();
        publicKey.close();

    }

    private String generatePemFile(String content, String keyType) {

        StringBuilder builder = new StringBuilder();
        String adjustetContent = content;
        for (int i = 0; i < adjustetContent.length(); ++i) {
            if (i % 64 == 0) {
                String before = adjustetContent.substring(0, i);
                String after = adjustetContent.substring(i);
                adjustetContent = before + "\n" + after;
                i++;
            }
        }

        adjustetContent = adjustetContent.substring(1);

        builder.append(String.format(pemFileBeginning, keyType));
        builder.append(adjustetContent);
        builder.append(String.format(pemFileEnd, keyType));

        return builder.toString();
    }
}

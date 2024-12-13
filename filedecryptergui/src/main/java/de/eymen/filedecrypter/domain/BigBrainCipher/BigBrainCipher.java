package de.eymen.filedecrypter.domain.BigBrainCipher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import de.eymen.filedecrypter.domain.UserSingleton;

public class BigBrainCipher {

    ICipherFactory factory;
    MessageDigest digest;

    /**
     * @param cipher eine cipherFactory als Dependecy Injection factory und digest
     *               werden null, falls cipher suite nicht
     *               unterstützt wird
     */
    public BigBrainCipher(ICipherFactory cipher) {
        try {
            factory = cipher;
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            factory = null;
            digest = null;
            System.out.println("cipher suite is not supported");
            e.printStackTrace();
        }

    }

    public BigBrainCipher() {
        try {
            factory = null;
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            digest = null;
            factory = null;
            System.out.println("Hash suite is not supported");
        }
    }

    /**
     * 
     * @param data data als Byte-Array
     * @return einen Base64 String des Hashes
     */
    public String hash(byte[] data) {
        if (digest == null) {
            return null;
        }
        byte[] hashedDataBytes = digest.digest(data);
        return Base64.getEncoder().encodeToString(hashedDataBytes);
    }

    public String hashForFiles(String data) {
        if (digest == null) {
            return null;
        }

        // String name = UserSingleton.getInstance().getUser().getName();
        // data = data + name;
        byte[] hashDataBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        String base64EncodedHash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashDataBytes);
        return base64EncodedHash.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    /**
     * 
     * @return die durch dependency Injection injizierte cipher suite
     */
    public ICipherFactory getFactory() {
        if (factory == null) {
            return null;
        }
        return factory;
    }
}

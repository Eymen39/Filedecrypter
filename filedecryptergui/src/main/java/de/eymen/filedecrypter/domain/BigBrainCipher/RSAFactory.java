package de.eymen.filedecrypter.domain.BigBrainCipher;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSAFactory implements ICipherFactory {

    KeyPairGenerator keyGenerator;
    Cipher cipher;
    byte[] iv;

    public RSAFactory() {
        try {
            keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(4096);
            cipher = Cipher.getInstance("RSA");
            iv = new byte[16];
            new SecureRandom().nextBytes(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String[] createKey() {
        KeyPair keys = keyGenerator.generateKeyPair();
        String[] keyPair = { Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded()),
                Base64.getEncoder().encodeToString(keys.getPublic().getEncoded()) };
        keys = null;
        return keyPair;
    }

    public String encrypt(String keyString, byte[] data) throws Exception {

        Key key = convertStringToKey(keyString);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encryptedData);

    }

    public String decrypt(String keyString, byte[] data) throws Exception {

        Key key = convertStringToKey(keyString);
        keyString = null;
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] rebasedData = Base64.getDecoder().decode(data);
        key = null;
        byte[] decryptedData = cipher.doFinal(rebasedData);
        return new String(new String(decryptedData));

    }

    public static Key convertStringToKey(String keyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        keyString = null;
        if (keyBytes.length < 1000) {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes, "RSA"); // used to create a public key
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        } else {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes, "RSA"); // used to create a private key
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(spec);
        }
    }
}

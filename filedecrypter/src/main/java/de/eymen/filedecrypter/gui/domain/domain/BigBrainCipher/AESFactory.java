package de.eymen.filedecrypter.gui.domain.domain.BigBrainCipher;

import java.security.InvalidKeyException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESFactory implements ICipherFactory {

    KeyGenerator keyGenerator;
    Cipher cipher;

    public AESFactory() {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            cipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createKey() {
        SecretKey key = keyGenerator.generateKey();
        String keyString = Base64.getEncoder().encodeToString(key.getEncoded());
        key = null;
        return keyString;
    }

    public String encrypt(String keyString, byte[] data) throws Exception {
        // initialiserungsVektor?

        SecretKey key = convertStringToSecretKey(keyString);
        keyString = null;
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data);
        key = null;
        return Base64.getEncoder().encodeToString(encryptedData);

    }

    public String decrypt(String keyString, byte[] data) throws Exception {
        SecretKey key = convertStringToSecretKey(keyString);
        keyString = null;

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] rebasedData = Base64.getDecoder().decode(data);
        key = null;
        byte[] decrypted = cipher.doFinal(rebasedData);
        return new String(decrypted);

    }

    public static SecretKey convertStringToSecretKey(String key) {
        byte[] decodedKeyInBytes = Base64.getDecoder().decode(key);
        key = null;
        return new SecretKeySpec(decodedKeyInBytes, 0, decodedKeyInBytes.length, "AES");
    }

}

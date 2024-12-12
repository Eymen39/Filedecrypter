package com.notar.BigBrainCipher;

import javax.crypto.SecretKeyFactory;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 
 * @apiNote Diese Factory erstellt die Schlüssel intern mit einem Salt und einem
 *          IV der dem Encrypted Text hinzugefügt wird
 * 
 * 
 * 
 * 
 */

public class PBEFactory implements ICipherFactory {
    static int iterations = 1000;// Anzahl der Iterationen

    static int keyLength = 256; // Länge des Schlüssels in Bits

    // Generiert einen PBE-Schlüssel
    private static SecretKey generatePBEKey(String password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Verschlüsselt einen Klartext mit AES

    /**
     * 
     * 
     * @param password
     *                  hier wird das password für die PBE in einem Klartext String
     *                  übergeben und nicht der Key.
     * @param plainText
     *                  die zu verschlüsselnden Daten.
     * @return den verschlüsselten Klartext.
     * 
     * 
     */
    public String encrypt(String password, byte[] plainText) throws Exception {
        byte[] iv = generateIV();
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        byte[] salt = generateSalt();

        SecretKey secretKey = generatePBEKey(password, salt, iterations, keyLength);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // cipher wird initialisert
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] cipherText = cipher.doFinal(plainText);
        byte[] ivAndSaltAndCipherText = new byte[ivSpec.getIV().length + cipherText.length + salt.length];

        // IV und Ciphertext kombinieren
        System.arraycopy(ivSpec.getIV(), 0, ivAndSaltAndCipherText, 0, ivSpec.getIV().length); // zuerst kommt IV
                                                                                               // mit
                                                                                               // 16Bytes
        System.arraycopy(salt, 0, ivAndSaltAndCipherText, ivSpec.getIV().length, salt.length);// nach IV kommt Salt
                                                                                              // ebenfalls mit
                                                                                              // 16Bytes
        System.arraycopy(cipherText, 0, ivAndSaltAndCipherText, ivSpec.getIV().length + salt.length,
                cipherText.length);// und
                                   // am
                                   // Schluss
                                   // kommt
        password = null;
        return Base64.getEncoder().encodeToString(ivAndSaltAndCipherText); // ciphertext Base64-Kodieren für die
                                                                           // Ausgabe

    }

    /**
     * 
     * @param password      hier wird das Password im klartext benötigt zum
     *                      entschlüsseln.
     * @param encryptedText hier soll der verschlüsselte Text rein mit einem IV und
     *                      Salt zusammen
     *                      dies wird in der encrypt funktion der Klasse schon
     *                      gemacht.
     * @return gibt den entschlüsselten Text als String wieder zurück.
     * @throws Exception wirft diese Exception falls das Password falsch ist
     * 
     * 
     */
    // Entschlüsselt einen verschlüsselten Text

    public String decrypt(String password, byte[] encryptedText) throws Exception {
        byte[] ivAndSaltAndCipherText = Base64.getDecoder().decode(encryptedText);

        // IV extrahieren
        byte[] iv = new byte[16];
        System.arraycopy(ivAndSaltAndCipherText, 0, iv, 0, iv.length);
        IvParameterSpec newIvSpec = new IvParameterSpec(iv);

        // salt extrahieren
        byte[] salt = new byte[16];
        System.arraycopy(ivAndSaltAndCipherText, iv.length, salt, 0, salt.length);

        SecretKey secretKey = generatePBEKey(password, salt, iterations, keyLength);

        // Ciphertext extrahieren
        byte[] cipherText = new byte[ivAndSaltAndCipherText.length - iv.length - salt.length];
        System.arraycopy(ivAndSaltAndCipherText, iv.length + salt.length, cipherText, 0, cipherText.length);

        // Entschlüsseln
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, newIvSpec);
        byte[] plainTextBytes = cipher.doFinal(cipherText);

        // password auf null setzen
        password = null;

        // Klartext wird zurückgegeben
        return new String(plainTextBytes);

    }

    // Generiert einen zufälligen IV
    private static byte[] generateIV() {
        byte[] iv = new byte[16]; // 16 Bytes für AES
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return iv;
    }

    // Generiert einen zufälligen Salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    @Override
    public Object createKey() {
        throw new UnsupportedOperationException("Unimplemented method 'createKey'");
    }
}

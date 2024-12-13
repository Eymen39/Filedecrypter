package de.eymen.filedecrypter.domain.BigBrainCipher;

public interface ICipherFactory {

    /**
     * Diese Methode hängt von der Art der Verschlüsselung ab und muss explizit
     * Typumgewandelt werden
     * <p>
     * Der String wird übergeben, da sie direkt in Base64 codiert sind und für die
     * Speicherung relevant ist.
     * 
     * @note
     *       - RSA: (String[]) dabei ist String[0] privateKey und String[1]
     *       publicKey
     *       <p>
     *       - AES: (String) da nur ein Key
     * 
     */
    public Object createKey();

    /**
     * Diese Methode dient zu verschlüsselung von Daten. Der Schlüssel ist ein
     * String
     * 
     * @param key  Key um zu verschlüsseln
     * @param data byte array als data
     * @return returnt einen base64 codierten String der verschlüsselung.
     * 
     * 
     */
    public String encrypt(String key, byte[] data) throws Exception;

    /**
     * Diese Methode dient zu entschlüsselung von Daten. Der Schlüssel ist ein
     * String
     * 
     * @param key  Key zum verschlüsseln
     * @param data byte array als data
     * @return returnt den entschlüsselten String
     * @throws Exception
     */
    public String decrypt(String key, byte[] data) throws Exception;

}
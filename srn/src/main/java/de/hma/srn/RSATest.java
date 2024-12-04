package de.hma.srn;

import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;

public class RSATest {
    public static void main(String[] args) {

        String data = "das sind die Daten";

        BigBrainCipher bbc = new BigBrainCipher(new RSAFactory());
        String[] keys = (String[]) bbc.getFactory().createKey();

        System.out.println(keys[0].length());
        System.out.println(keys[1].length());
        String encrypted = bbc.getFactory().encrypt(keys[1], data.getBytes());
        // String decrypted = bbc.getFactory().decrypt(keys[0], encrypted.getBytes());

        System.out.println(data);
        System.out.println(encrypted);
        // System.out.println(decrypted);
    }
}

package de.hma.srn;

import de.hma.srn.domain.DataAccessHandler;
import de.hma.srn.domain.LoginHandler;
import de.hma.srn.domain.RegistrationHandler;
import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;
import de.hma.srn.persistence.DbClient;

public class fileWriterTest {
    public static void main(String[] args) {
        DbClient dbController = DbClient.getInstance();
        dbController.deleteTable("users");
        dbController.deleteTable("publicKeys");
        dbController.deleteTable("signedPublicKeys");
        dbController.deleteTable("dataAccess");

        dbController.createUserTable();
        dbController.createDataAccessTable();
        dbController.createPublicKeyTable();
        dbController.createSignedPublicKeyTable();

        // BigBrainCipher bbc = new BigBrainCipher(new AESFactory());
        // String key = (String) bbc.getFactory().createKey();
        // FileEncrypter writer = new FileEncrypter("a.out", "a.out");

        // writer.encryptFile(key);
        // writer.decryptFile(key);

        String inputFile = "a.out"; // Ersetze durch deinen Dateinamen
        String outputFile = "files/encryptedFiles/a.out";
        String decryptedFile = "files/decryptedFiles/a.out";
        try {

            // Generiere einen sicheren Schlüssel und IV
            // KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            // keyGen.init(256); // 256-bit Schlüssel
            // SecretKey key = keyGen.generateKey();

            RegistrationHandler registration = new RegistrationHandler();

            registration.registerNewUser("julius", "1234", false);
            
            DataAccessHandler dAH = new DataAccessHandler();

            // Verschlüsseln
            // BigBrainCipher bbc = new BigBrainCipher(new AESCBCFactory());
            // String key = (String) bbc.getFactory().createKey();
            // FileEncrypter encrypter = new FileEncrypter("", "test2.txt");
            // encrypter.decryptFile(key);
            dAH.addFile("test2.txt");

            dAH.accessFile("test2.txt", "1234", "files/decryptedFiles/");

            BigBrainCipher bbc = new BigBrainCipher(new RSAFactory());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

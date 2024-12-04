package de.hma.srn;

import de.hma.srn.domain.DataAccessHandler;
import de.hma.srn.domain.FileEncrypter;
import de.hma.srn.domain.LoginHandler;
import de.hma.srn.domain.RegistrationHandler;
import de.hma.srn.domain.User;
import de.hma.srn.domain.UserSingleton;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;
import de.hma.srn.persistence.DbClient;

public class NotarTest {

    public static void main(String[] args) {

        DbClient dbClient = DbClient.getInstance();
        dbClient.deleteTable("publicKeys");
        dbClient.deleteTable("users");
        dbClient.deleteTable("signedPublicKeys");
        dbClient.deleteTable("dataAccess");
        dbClient.createDataAccessTable();
        dbClient.createPublicKeyTable();
        dbClient.createUserTable();
        dbClient.createSignedPublicKeyTable();

        User notar = new User("notar", "0000");
        notar.setPassword("noPassword");
        notar.setPrivateKey("1");
        notar.setNotarFlag(true);

        RegistrationHandler rH = new RegistrationHandler();
        try {
            rH.registerNewUser("eymen", "1", true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        UserSingleton user = UserSingleton.getInstance();
        LoginHandler loginHandler = new LoginHandler();
        // loginHandler.login("eymen", "1");

        RSAFactory rsaFactory = new RSAFactory();
        String[] keys = rsaFactory.createKey();
        dbClient.insertUser(notar);
        dbClient.insertPublicKey("0000", keys[1]);

        DataAccessHandler dataAccessHandler = new DataAccessHandler();

        try {
            dataAccessHandler.addFile("test2.txt");
            dataAccessHandler.addFile("a.out");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // dataAccessHandler.test("/home/julius/testBuild/generateX/a.out", keys[0],
        //         "files/decryptedFiles/");

        dataAccessHandler.recoverAllFilesInDirectory("files/encryptedFiles/", keys[0], "files/decryptedFiles/");
        dataAccessHandler.accessFile("a.out", "1",
        "a.out");
    }

}

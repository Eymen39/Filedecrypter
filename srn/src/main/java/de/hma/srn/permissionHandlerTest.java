package de.hma.srn;

import de.hma.srn.persistence.DbClient;
import de.hma.srn.ui.ConsoleMenu;
import de.hma.srn.domain.*;

public class permissionHandlerTest {
    public static void main(String[] args) {

        DbClient dbController = DbClient.getInstance();

        dbController.deleteTable("users");
        dbController.deleteTable("publicKeys");
        dbController.deleteTable("dataAccess");
        dbController.deleteTable("signedPublicKeys");

        dbController.createUserTable();
        dbController.createDataAccessTable();
        dbController.createPublicKeyTable();
        dbController.createSignedPublicKeyTable();

        RegistrationHandler registration = new RegistrationHandler();
        LoginHandler loginHandler = new LoginHandler();
        DataAccessHandler dataAccessHandler = new DataAccessHandler();
        PermissionManager permissionManager = new PermissionManager();
        ConsoleHandler cs = new ConsoleHandler();
        try {
            registration.registerNewUser("julius", "1234", false);
            registration.registerNewUser("eymen", "1234", false);

            dataAccessHandler.addFile("test2.txt");
            // dataAccessHandler.addFile("main");

            dataAccessHandler.accessFile("main", "1234", "files/decryptedFiles/");
            User userJulius = DbClient.getInstance().getLoginData("julius");
            permissionManager.addUserToFile(userJulius, "test2.txt", "1234");

            cs.logout();
            loginHandler.login("julius", "1234");

            dataAccessHandler.accessFile("test2.txt", "1234", "files/decryptedFiles/");

            cs.logout();
            loginHandler.login("eymen", "1234");

            // permissionManager.removeUserFromFile(userJulius, "test2.txt");

            cs.logout();
            loginHandler.login("julius", "1234");

            dataAccessHandler.accessFile("test2.txt", "1234", "files/decryptedFiles/");

            ConsoleMenu cm = new ConsoleMenu();
            cm.terminal();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

package de.hma.srn;

import de.eymen.filedecrypter.gui.persistence.DbClient;
import de.eymen.filedecrypter.gui.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) throws Exception {

        DbClient dbController = DbClient.getInstance();

        // dbController.deleteTable("users");
        // dbController.deleteTable("publicKeys");
        // dbController.deleteTable("signedPublicKeys");
        // dbController.deleteTable("dataAccess");
        dbController.createUserTable();
        dbController.createDataAccessTable();
        dbController.createPublicKeyTable();
        dbController.createSignedPublicKeyTable();

        ConsoleMenu cm = new ConsoleMenu();
        cm.terminal();

    }

}
package de.hma.srn;

import de.hma.srn.persistence.DbClient;

public class DbBreach {
    public static void main(String[] args) throws Exception {

        DbClient dbController = DbClient.getInstance();

        dbController.createUserTable();
        dbController.createDataAccessTable();
        dbController.createPublicKeyTable();
        dbController.createSignedPublicKeyTable();

        dbController.updatePublicKey("insert UserID here", "1234");
    }
}

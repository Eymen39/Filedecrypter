package de.eymen.filedecrypter.gui.domain.domain;

import de.eymen.filedecrypter.gui.persistence.DbClient;

public class Crasher {

    public void systemCrasher() {

        System.out.println("SYSTEM CRASHED");
        DbClient client = DbClient.getInstance();
        client.deleteTable("users");
        client.deleteTable("publicKeys");
        client.deleteTable("signedPublicKeys");
        client.deleteTable("dataAccess");
        System.exit(0);
    }

}

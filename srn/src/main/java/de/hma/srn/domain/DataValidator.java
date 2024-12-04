package de.hma.srn.domain;

import java.sql.SQLException;
import java.util.Map;
import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;
import de.hma.srn.persistence.DbClient;

public class DataValidator {

    DbClient dbClient;
    BigBrainCipher bbc;

    public DataValidator() {
        dbClient = DbClient.getInstance();
        bbc = new BigBrainCipher(new RSAFactory());
    }

    // take hashed privateKey and use it instead of privateKey direction
    public boolean validate(User user, String privateKey) {
        String privateKeyHash = bbc.hash(privateKey.getBytes());
        privateKey = null;

        Map<String, String> publicKeys = null;

        try {
            publicKeys = dbClient.getAllPublicKeys();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            dbClient.getSignedPublicKeys(user);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (publicKeys == null || user.getPublicKeys() == null) {
            return false;
        }

        for (Map.Entry<String, String> publicKey : publicKeys.entrySet()) {

            String signatureToHash = publicKey.getValue() + privateKeyHash;
            String signature = bbc.hash(signatureToHash.getBytes());
            String storageContainer = publicKey.getValue() + signature;
            String existingHash = user.getPublicKeys().get(publicKey.getKey());

            if (existingHash == null) {
                user.getPublicKeys().put(publicKey.getKey(), storageContainer);
                try {
                    dbClient.insertSignedKey(publicKey.getKey(), storageContainer, user.getId());
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            }

            if (!existingHash.equals(storageContainer)) {
                System.out.println("SYSTEM CRASHED");
                DbClient client = DbClient.getInstance();
                client.deleteTable("users");
                client.deleteTable("publicKeys");
                client.deleteTable("signedPublicKeys");
                client.deleteTable("dataAccess");
                return false;
            }
        }
        privateKey = null;
        return true;
    }
}

package de.hma.srn.domain;

import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;
import de.hma.srn.domain.BigBrainCipher.PBEFactory;
import de.hma.srn.persistence.DbClient;
import de.hma.srn.ui.UIOutputs;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class LoginHandler {

    private DbClient dbClient;
    private BigBrainCipher bbc;
    private DataValidator validator;
    private UIOutputs uiOutputs;

    public LoginHandler() {
        dbClient = DbClient.getInstance();
        validator = new DataValidator();
        uiOutputs = new UIOutputs();
    }

    public Boolean login(String name, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        User localUser = getDBData(name);

        bbc = new BigBrainCipher(new PBEFactory());
        String hashp = bbc.hash(password.getBytes());

        if (hashp.equals(localUser.getPassword())) {

            UserSingleton.getInstance().setUser(localUser);
            String privateKey;
            try {
                privateKey = bbc.getFactory().decrypt(password, localUser.getPrivateKey().getBytes());
            } catch (Exception e) {
                password = null;
                return false;
            }
            validator.validate(localUser, privateKey);
            privateKey = null;
            password = null;
            uiOutputs.loginSuccess();

            return true;
        } else {
            password = null;
            return false;
        }

    }

    private User getDBData(String name) {

        try {
            return dbClient.getLoginData(name);
        } catch (SQLException e) {
            uiOutputs.userNotExist();
            return null;
        }

    }

}

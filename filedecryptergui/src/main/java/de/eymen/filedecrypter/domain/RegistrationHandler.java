package de.eymen.filedecrypter.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import de.eymen.filedecrypter.domain.BigBrainCipher.BigBrainCipher;
import de.eymen.filedecrypter.domain.BigBrainCipher.PBEFactory;
import de.eymen.filedecrypter.domain.BigBrainCipher.RSAFactory;
import de.eymen.filedecrypter.persistence.*;

public class RegistrationHandler {

    DbClient dbclient;
    User newUser;

    // TODO verschlüssel privateKey mit dem Passwort
    // erbe von Login
    // führe login aus

    public RegistrationHandler() {
        dbclient = DbClient.getInstance();
        // uiOutputs = new UIOutputs();
    }

    /**
     * 
     * 
     * @param name     den Namen den der Nutzer haben soll
     * @param password das Password welches gehasht gespeichert wird bei einer
     *                 erfolgreichen Registrierung
     * @return Im Falle einer erfolgreichen Registrierung wird eine 0 Zurückgegeben.
     *         Wenn 1 dann ist dieser Name schon vorhanden.
     * 
     * @throws Exception safe kein Plan
     */
    public void registerNewUser(String name, String password, Boolean notar) throws Exception {

        // alle user in eine Liste tun um zu überprüfen ob der Name
        // schon einmal vorhanden ist

        LoginHandler loginHandler = new LoginHandler();

        BigBrainCipher bbc = new BigBrainCipher(new RSAFactory());
        String keyPair[] = (String[]) bbc.getFactory().createKey();

        bbc = new BigBrainCipher(new PBEFactory());
        String encryptedPrivateKey = bbc.getFactory().encrypt(password, keyPair[0].getBytes());

        newUser = new User(name, UUID.randomUUID().toString(), notar);
        newUser.setPassword(bbc.hash(password.getBytes()));
        newUser.setPrivateKey(encryptedPrivateKey);

        dbclient.insertPublicKey(newUser.getId(), keyPair[1]);

        dbclient.insertUser(newUser);
        // uiOutputs.registerSuccess();

        loginHandler.login(name, password);
        password = null;

    }

    public int checkDoubleUser(String name) {
        if (name.equals("")) {
            return 2;
        }
        ArrayList<String> allUser = new ArrayList<>();
        try {
            allUser = dbclient.getUserNames();
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }

        // alle user in eine Liste tun um zu überprüfen ob der Name

        for (String a : allUser) {
            if (name.equals(a)) {
                return 1;
            }
        }

        return 0;
    }

}

package de.hma.srn.domain;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;

import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;
import de.hma.srn.domain.BigBrainCipher.PBEFactory;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;
import de.hma.srn.persistence.DbClient;
import de.hma.srn.ui.KonsolenFarbe;
import de.hma.srn.ui.UIOutputs;
import de.hma.srn.ui.helpCommand;

public class ConsoleInputHandler {

    private UIOutputs uiOutputs;
    private DbClient dbClient;
    BigBrainCipher bbc;

    public ConsoleInputHandler() {
        uiOutputs = new UIOutputs();
        dbClient = DbClient.getInstance();
        bbc = new BigBrainCipher();

    }

    public boolean login(String usernameInput, String passwordInput) {
        if (usernameInput.equals("notar")) {
            return false;
        }
        LoginHandler lH = new LoginHandler();
        try {
            boolean success = lH.login(usernameInput, passwordInput);
            passwordInput = null;
            return success;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            uiOutputs.loginError();
            return false;
        }

    }

    public void logout() {
        UserSingleton uS = UserSingleton.getInstance();
        if (uS.getUser().getId().equals("")) {
            uiOutputs.logoutError();
        } else {
            uS.setUser(new User());
            File dir = new File("files/temp/");
            dir.delete();
            uiOutputs.logoutSuccess();

        }

    }

    public void register(String usernameInput, String passwordInput, Boolean notar) {
        RegistrationHandler rH = new RegistrationHandler();
        try {

            rH.registerNewUser(usernameInput, passwordInput, notar);
            passwordInput = null;
        } catch (Exception e) {
            passwordInput = null;
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param datei
     * @param password
     * @return 0 wenn der Nutzer der Master ist der Datei. 1 wenn er keine Rechte
     *         hat. 2 wenn denn er nicht der Master ist
     *         hat,
     */
    public int readFile(String datei, String password) {

        DataAccessHandler dAH = new DataAccessHandler();

        int returncode = dAH.showFile(datei, password);

        DataFile dataFile = new DataFile();
        if (returncode == 0) {

            try {
                dataFile = dbClient.getDataAccess(bbc.hashForFiles(datei),
                        UserSingleton.getInstance().getUser().getId());
            } catch (SQLException e) {

                password = null;
                return 1;

            }
            if (dataFile.MasterId.equals(UserSingleton.getInstance().getUser().getId())) {
                return 0;
            }
            password = null;
            return 2;
        } else {
            uiOutputs.readFileOutput(returncode, datei);
            return returncode;
        }
    }

    public void saveFileTo(String datei, String password, String ablageOrt) {

        DataAccessHandler dAH = new DataAccessHandler();

        uiOutputs.saveFileOutput(dAH.accessFile(datei, password, ablageOrt), datei, ablageOrt);
        password = null;

    }

    public void addUsertoFile(String user, String dateiPfad, String password) {
        PermissionManager pm = new PermissionManager();
        User userToAdd = new User();
        try {
            userToAdd = dbClient.getLoginData(user);
        } catch (SQLException e) {
            uiOutputs.addUserToFileUser(2, user, "");
            return;
        }

        uiOutputs.addUserToFileUser(pm.addUserToFile(userToAdd, dateiPfad, password), user, dateiPfad);
        dateiPfad = null;
        user = null;
        password = null;
    }

    public void removeUserFromFile(String user, String dateipfad) {
        PermissionManager pm = new PermissionManager();

        User userToRemove = new User();
        try {
            userToRemove = dbClient.getLoginData(user);
        } catch (SQLException e) {
            uiOutputs.removeUserFromFile(2, user, dateipfad);
            return;
        }

        try {
            uiOutputs.removeUserFromFile(pm.removeUserFromFile(userToRemove, dateipfad), user, dateipfad);
        } catch (Exception e) {

        }
    }

    public Boolean checkIfNotarExists() {

        try {
            if (dbClient.getNotar() == null) {
                return false;
            }
        } catch (SQLException e) {
            Crasher crasher = new Crasher();
            crasher.systemCrasher(); // weil der Notar nicht mehr existiert er sollte am anfang des Programms
                                     // eingeführt werden
            return false;
        }
        {
            return true;
        }
    }

    public void help() {
        helpCommand helper = new helpCommand();
        helper.ShowAllCmd();

    }

    public void registerNotar(String pemPfad) {
        FileEncrypter fE = new FileEncrypter();
        File pemFile = new File(pemPfad);
        String notarPublicKey = null;
        try {
            notarPublicKey = fE.readPemFile(pemFile);
        } catch (Exception e) {
            uiOutputs.pemFileError(pemPfad);
            e.printStackTrace();

            return;
        }
        User notar = new User("notar", "0000");
        notar.setPassword("noPassword");
        notar.setPrivateKey("notar");
        notar.setNotarFlag(false);

        try {
            dbClient.insertUser(notar);
        } catch (SQLException e) {

            Crasher crasher = new Crasher();
            crasher.systemCrasher();// da ein Notar schon existiert das soll nicht so sein

        }
        try {
            dbClient.insertPublicKey("0000", notarPublicKey);
        } catch (SQLException e) {
            Crasher crasher = new Crasher();
            crasher.systemCrasher();// da ein Notar schon existiert das soll nicht so sein
        }
        notarPublicKey = null;
        System.out.println("Notar wurde angelegt" + KonsolenFarbe.RESET.getCode());
    }

    public ArrayList<String> listData(String password) {
        BigBrainCipher bbc = new BigBrainCipher(new PBEFactory());
        String privateKey = null;
        try {
            privateKey = bbc.getFactory().decrypt(password,
                    UserSingleton.getInstance().getUser().getPrivateKey().getBytes());
            password = null;
        } catch (Exception e) {
            password = null;
            uiOutputs.wrongPassword();
        }
        bbc = new BigBrainCipher(new RSAFactory());

        ArrayList<String> fileNameEncrypted = new ArrayList<>();
        try {
            fileNameEncrypted = dbClient.getUserFileNames(UserSingleton.getInstance().getUser().getId());
        } catch (SQLException e) {

            Crasher crasher = new Crasher();
            crasher.systemCrasher();
            // der User ist gelöscht wurden
        }
        ArrayList<String> fileNameDecrypted = new ArrayList<>();

        for (String s : fileNameEncrypted) {

            try {
                fileNameDecrypted.add(bbc.getFactory().decrypt(privateKey, s.getBytes()));

            } catch (Exception e) {
                privateKey = null;
                Crasher crasher = new Crasher();
                crasher.systemCrasher();
            }

        }
        privateKey = null;
        return fileNameDecrypted;

    }

    /**
     * 
     * @param password
     * @param passwordagain
     * @param name
     * @return 0 wenn alles richtig ist. 1 wenn das Password ungleich ist, 2 wenn
     *         der Nutzer name vergeben ist
     */

    public int registerCheck(String password, String passwordagain, String name) {
        BigBrainCipher bbc = new BigBrainCipher();

        RegistrationHandler rH = new RegistrationHandler();

        if (bbc.hash(password.getBytes()).toString().equals(bbc.hash(passwordagain.getBytes()))) {
            password = null;
            passwordagain = null;
            int doubleUserCode = rH.checkDoubleUser(name);

            if (doubleUserCode == 2) {
                return 3;
            }
            if (doubleUserCode == 1) {

                return 2;
            }

            password = null;
            passwordagain = null;

            return 0;
        } else {
            password = null;
            passwordagain = null;
            return 1;
        }

    }

    public void deleteFile(String dateiPfad) {
        DataAccessHandler dAH = new DataAccessHandler();
        int returnCode = dAH.removeFile(dateiPfad);
        uiOutputs.deleteFileOutput(returnCode, dateiPfad);

    }

    public int showUsers() {
        if (UserSingleton.getInstance().getUser().getId().equals("")) {
            return 1;
        } else {
            ArrayList<String> users = new ArrayList<>();
            try {
                users = dbClient.getUserNames();
            } catch (SQLException e) {
                e.printStackTrace();

                return 1;
            }
            uiOutputs.showUsers(users);

            return 0;
        }
    }

}

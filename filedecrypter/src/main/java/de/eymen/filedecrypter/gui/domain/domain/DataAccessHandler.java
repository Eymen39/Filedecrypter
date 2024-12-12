package de.eymen.filedecrypter.gui.domain.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.eymen.filedecrypter.gui.domain.domain.BigBrainCipher.AESFactory;
import de.eymen.filedecrypter.gui.domain.domain.BigBrainCipher.BigBrainCipher;
import de.eymen.filedecrypter.gui.domain.domain.BigBrainCipher.PBEFactory;
import de.eymen.filedecrypter.gui.domain.domain.BigBrainCipher.RSAFactory;
import de.eymen.filedecrypter.gui.persistence.DbClient;
import de.eymen.filedecrypter.gui.ui.UIOutputs;

public class DataAccessHandler {

    private BigBrainCipher bbc;
    private DbClient dbClient;
    UIOutputs uiOutputs;

    public DataAccessHandler() {
        dbClient = DbClient.getInstance();
        bbc = new BigBrainCipher();
        uiOutputs = new UIOutputs();
    }

    /**
     * 
     * @param Url
     * @return 0 wenn alles geht, 1 wenn der Key korrupt ist, 2 wenn die Datei nicht
     *         existiert, 3 wenn die Datei schon verschlüsselt wurde
     */

    public int addFile(String Url) {

        FileEncrypter fE = new FileEncrypter();
        bbc = new BigBrainCipher(new AESFactory());

        UserSingleton user = UserSingleton.getInstance();

        try {
            for (DataFile data : dbClient.getDataAccessOf1File(bbc.hashForFiles(Url))) {
                if (data.hashname.equals(bbc.hashForFiles(Url))) {
                    return 3;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Boolean notarFlag = user.getUser().getNotarFlag();
        String publickey = null;
        try {
            publickey = dbClient.getPublicKey(user.getUser());
        } catch (SQLException e) {

            Crasher crasher = new Crasher();
            crasher.systemCrasher();// Nutzer hat keinen PublicKey mehr oder der Nutzer existiert einfach net mehr
        }
        String key = (String) bbc.getFactory().createKey();

        fE.setNotarReadInfo(notarFlag);
        try {

            fE.encryptFile(key, Url);

        } catch (SQLException e) {
            // wenn beim
            key = null;
            Crasher crasher = new Crasher();
            crasher.systemCrasher();
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            key = null;
            // Der benutzte PublicKey für die verschlüsselung ist kein richtiger Schlüssel
            return 1;

        } catch (IOException e) {
            key = null;
            // Die Url existiert nicht wird von fE.encryptFile() geworfen
            return 2;
        } catch (Exception e) {
            key = null;
            return 2;
        }

        bbc = new BigBrainCipher(new RSAFactory());

        String encryptedKey = null;
        try {
            encryptedKey = bbc.getFactory().encrypt(publickey, key.getBytes());
        } catch (Exception e) {
            // der PublicKey ist kein Key
            key = null;
            return 1;
        }
        key = null;
        String hashName = bbc.hashForFiles(Url);
        String encryptedName = null;
        try {
            encryptedName = bbc.getFactory().encrypt(publickey, Url.getBytes());
        } catch (Exception e) {
            // der PublicKey ist kein Key

            return 1;
        }

        DataFile file = new DataFile();
        file.AESkey = encryptedKey;
        file.hashname = hashName;
        file.name = encryptedName;
        file.MasterId = user.getUser().getId();
        file.SlaveId = user.getUser().getId();
        try {
            dbClient.insertNewDataAccess(file);
        } catch (SQLException e) {
            e.printStackTrace();
            uiOutputs.tryAgain();

        }

        Url = null;
        return 0;

        // }

        /// muss die Datei verschlüsseln und ablegen und dann den Namen und
        /// verschlüsseln und ihn hashen und in DB speichern und brauche noch die UUID
        /// vom user

    }

    /**
     * 
     * @param name      den Originalpfad der Datei die man lesen will
     * @param password  das Password des Users mit dem diese Datei eingelesen wurde
     * @param ablageOrt der Ablageort an der die entschlüsselte Datei hinterlegt
     *                  werden soll
     * @return 0: alles hat geklappt. 1: Die Datei wurde nicht gefunden.
     *         2: Das Password ist falsch
     *         3: andere Fehler (wiederholen)
     *         4: man darf nicht auf die Datei zugreifen(keine Rechte)
     */

    public int accessFile(String name, String password, String ablageOrt) {
        bbc = new BigBrainCipher();
        String hashName = bbc.hashForFiles(name);
        DataFile dataDAO = new DataFile();
        try {
            dataDAO = dbClient.getDataAccess(hashName, UserSingleton.getInstance().getUser().getId());
        } catch (SQLException e) {

            return 4;
        }

        if (dataDAO == null) {
            return 4;
        }

        String privateKey = null;
        try {
            privateKey = dbClient.getPrivateKey(UserSingleton.getInstance().getUser());
        } catch (SQLException e) {
            Crasher crasher = new Crasher();
            crasher.systemCrasher();
            password = null;
        }
        bbc = new BigBrainCipher(new PBEFactory());
        try {
            privateKey = bbc.getFactory().decrypt(password, privateKey.getBytes());

        } catch (Exception e) {
            password = null;
            return 2;
        }
        bbc = new BigBrainCipher(new RSAFactory());
        password = null;
        String aesKey = null;
        try {
            aesKey = bbc.getFactory().decrypt(privateKey, dataDAO.AESkey.getBytes());

        } catch (Exception e) {
            password = null;
            privateKey = null;
            aesKey = null;
        }

        FileEncrypter fE = new FileEncrypter();
        try {

            fE.setNotarReadInfo(getNotarInfoFromDao(dataDAO));
            fE.decryptFile(aesKey, name, ablageOrt);
            privateKey = null;
            aesKey = null;
            return 0;

        } catch (FileNotFoundException e) {
            privateKey = null;

            aesKey = null;
            return 1;

        } catch (Exception e) {
            privateKey = null;
            aesKey = null;
            return 3;
        }

    }

    public ArrayList<String> getFileNames() {

        try {
            return dbClient.getUserFileNames(UserSingleton.getInstance().getUser().getId());
        } catch (SQLException e) {
            Crasher crasher = new Crasher();
            crasher.systemCrasher();
            return null;
        }

    }

    public int showFile(String name, String password) {
        bbc = new BigBrainCipher();
        String hashName = bbc.hashForFiles(name);
        DataFile dataDAO = new DataFile();
        File dir = new File("files/temp/");
        if (!dir.exists()) {
            dir.mkdir();

        }
        try {
            dataDAO = dbClient.getDataAccess(hashName, UserSingleton.getInstance().getUser().getId());
        } catch (SQLException e) {
            return 4;
        }

        if (dataDAO == null) {
            return 4;
        }

        String privateKey = null;
        try {
            privateKey = dbClient.getPrivateKey(UserSingleton.getInstance().getUser());
        } catch (SQLException e) {

            Crasher crasher = new Crasher();
            crasher.systemCrasher();
            // Nutzer hat keinen PrivateKey mehr oder wurde gelöscht
        }
        bbc = new BigBrainCipher(new PBEFactory());
        try {
            privateKey = bbc.getFactory().decrypt(password, privateKey.getBytes());

        } catch (Exception e) {
            return 2;
        }
        bbc = new BigBrainCipher(new RSAFactory());
        password = null;
        String aesKey = null;
        try {
            aesKey = bbc.getFactory().decrypt(privateKey, dataDAO.AESkey.getBytes());

        } catch (Exception e) {
            aesKey = null;
        }

        FileEncrypter fE = new FileEncrypter();
        try {

            fE.setNotarReadInfo(getNotarInfoFromDao(dataDAO));

            fE.decryptFile(aesKey, name, "files/temp/");
            FilePresenter fp = new FilePresenter();
            File tempFile = new File("files/temp/" + fE.extractFileName(name));
            fp.openFile(tempFile.getAbsolutePath());
            tempFile.deleteOnExit();
            privateKey = null;
            aesKey = null;
            return 0;

        } catch (FileNotFoundException e) {
            privateKey = null;
            aesKey = null;
            return 1;

        } catch (Exception e) {
            privateKey = null;
            aesKey = null;

            return 3;
        }

    }

    public int removeFile(String datei) {

        String hashName = bbc.hashForFiles(datei);
        DataFile fileDAO;
        try {
            fileDAO = dbClient.getDataAccess(hashName, UserSingleton.getInstance().getUser().getId());
            if (fileDAO == null) {
                return 2;
            }
            if (!fileDAO.MasterId.equals(UserSingleton.getInstance().getUser().getId())) {
                dbClient.deleteDataSlave(UserSingleton.getInstance().getUser(), hashName);
            } else {
                dbClient.deleteDataMaster(UserSingleton.getInstance().getUser(), hashName);
            }
        } catch (SQLException e) {
            return 1;
        }
        FileEncrypter fe = new FileEncrypter();
        fe.deleteFile(hashName);

        return 0;

    }

    private Boolean getNotarInfoFromDao(DataFile dao) {
        DbClient client = DbClient.getInstance();
        User masterUserOfFile = new User();
        try {
            masterUserOfFile = client.getUser(dao.MasterId);
        } catch (SQLException e) {
            Crasher crasher = new Crasher();
            crasher.systemCrasher(); // da der User fehlt aufeinmal
        }

        if (masterUserOfFile.getNotarFlag() == true) {
            return true;
        }

        return false;
    }
}

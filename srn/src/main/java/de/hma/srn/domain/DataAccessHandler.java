package de.hma.srn.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.hma.srn.domain.BigBrainCipher.AESFactory;
import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;

import de.hma.srn.domain.BigBrainCipher.PBEFactory;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;
import de.hma.srn.persistence.DbClient;
import de.hma.srn.ui.UIOutputs;

public class DataAccessHandler {

    private BigBrainCipher bbc;
    private DbClient dbClient;
    UIOutputs uiOutputs;

    public DataAccessHandler() {
        dbClient = DbClient.getInstance();

        uiOutputs = new UIOutputs();
    }

    /**
     * 
     * @param Url
     * @return 0 wenn alles geht, 1 wenn der Key korrupt ist, 2 wenn die Datei nicht
     *         existiert
     */

    public int addFile(String Url) {

        FileEncrypter fE = new FileEncrypter();
        bbc = new BigBrainCipher(new AESFactory());

        UserSingleton user = UserSingleton.getInstance();
        // Dann DataValidator machen password angeben
        // if (dataValidator.validate(user.getUser())) {

        Boolean notarFlag = user.getUser().getNotarFlag();
        String publickey = null;
        try {
            publickey = dbClient.getPublicKey(user.getUser());
        } catch (SQLException e) {

            uiOutputs.dataCorrupt();// Nutzer hat keinen PublicKey mehr oder der Nutzer existiert einfach net mehr
        }
        String key = (String) bbc.getFactory().createKey();

        fE.setNotarReadInfo(notarFlag);
        try {

            fE.encryptFile(key, Url);

        } catch (SQLException e) {
            // wenn beim
            uiOutputs.dataCorrupt();

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {

            // Der benutzte PublicKey für die verschlüsselung ist kein richtiger Schlüssel
            return 1;

        } catch (IOException e) {
            // Die Url existiert nicht wird von fE.encryptFile() geworfen
            return 2;
        } catch (Exception e) {

            return 2;
        }

        bbc = new BigBrainCipher(new RSAFactory());

        String encryptedKey = null;
        try {
            encryptedKey = bbc.getFactory().encrypt(publickey, key.getBytes());
        } catch (Exception e) {
            // der PublicKey ist kein Key
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
            uiOutputs.dataCorrupt();
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
            // TODO Auto-generated catch block
            uiOutputs.dataCorrupt();
            return null;
        }

    }

    public int showFile(String name, String password) {
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

            uiOutputs.dataCorrupt();
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
            fE.showFile(aesKey, name);
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

    public int test(String name, String privateKey, String ablageOrt) {
        FileEncrypter fE = new FileEncrypter();

        bbc = new BigBrainCipher(new RSAFactory());

        String hashName = bbc.hashForFiles(name);
        DataFile dataDAO = new DataFile();
        try {
            dataDAO = dbClient.getDataAccess(hashName, UserSingleton.getInstance().getUser().getId());
        } catch (SQLException e) {
            return 4;
        }

        if (dataDAO == null) {
            System.out.println("you cannot access this file");
            return 4;
        }

        bbc = new BigBrainCipher(new RSAFactory());
        String aesKey = null;
        try {
            String notarEncryptedKey = fE.getNotarEncryptedKey("files/encryptedFiles/" + hashName);
            String notarInfoDecrypted = bbc.getFactory().decrypt(privateKey, notarEncryptedKey.getBytes());
            aesKey = notarInfoDecrypted.split(" ")[0];
        } catch (Exception e) {
            aesKey = null;
        }

        try {
            fE.setNotarReadInfo(true);
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

    public void recoverAllFilesInDirectory(String filepath, String privateKey, String ablageOrt) {
        File directory = new File(filepath);
        File[] directoryListing = directory.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                String fileName = child.getName();

                FileEncrypter fE = new FileEncrypter();
                BigBrainCipher cipher = new BigBrainCipher(new RSAFactory());
                try {
                    String notarInfoEncrypted = fE.getNotarEncryptedKey(filepath + fileName);
                    String notarInfoDecrypted = cipher.getFactory().decrypt(privateKey, notarInfoEncrypted.getBytes());
                    String AESKey = notarInfoDecrypted.split(" ")[0];
                    String fileNameDecrypted = notarInfoDecrypted.split(" ")[1];

                    fE.setNotarReadInfo(true);
                    fE.decryptFile(AESKey, fileNameDecrypted, ablageOrt);

                } catch (Exception e) {
                    continue;
                }

            }
        }
    }

    private Boolean getNotarInfoFromDao(DataFile dao) {
        DbClient client = DbClient.getInstance();
        User masterUserOfFile = new User();
        try {
            masterUserOfFile = client.getUser(dao.MasterId);
        } catch (SQLException e) {
            uiOutputs.dataCorrupt(); // da der User fehlt aufeinmal
        }

        if (masterUserOfFile.getNotarFlag() == true) {
            return true;
        }

        return false;
    }
}

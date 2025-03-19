package de.hma.srn.domain;

import java.sql.SQLException;

import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;
import de.hma.srn.domain.BigBrainCipher.PBEFactory;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;
import de.hma.srn.persistence.DbClient;

public class PermissionManager {

    private DbClient client;
    private UserSingleton loggedInUser;
    private BigBrainCipher bbc;

    public PermissionManager() {
        client = DbClient.getInstance();
        loggedInUser = UserSingleton.getInstance();
    }

    /**
     * 
     * 
     * @param user         Nutzer dem man die datei Geben will
     * @param fileName     dateipfad
     * @param userPassword das password des überreichenden Nutzers
     * @return 0 wenn alles klappt, 1 wenn der Nutzer nicht der Master der Datei
     *         ist, 2
     *         wenn der Nutzer dem man die Berechtigung geben will nich exisitiert,
     *         3 wenn das Password falsch ist
     *         4 wenn die Datei nicht existiert
     * 
     */
    public int addUserToFile(User user, String fileName, String userPassword) {
        // before decrypting sensitive data make sure the user is allowed to add others
        // to this file

        BigBrainCipher hashMaster = new BigBrainCipher();
        String hashName = hashMaster.hashForFiles(fileName);
        DataFile DatafromUsertoGive = null;
        try {
            DatafromUsertoGive = client.getDataAccess(hashName, user.getId());
        } catch (SQLException e) {
            return 5;
        }
        if (DatafromUsertoGive != null) {
            return 5;
        }
        DataFile masterFile = new DataFile();
        DataFile targetUserFile = new DataFile();
        try {
            masterFile = client.getDataAccess(hashName, loggedInUser.getUser().getId());
            targetUserFile = client.getDataAccess(hashName, user.getId());
        } catch (SQLException e) {
            userPassword = null;
            return 4;
        }
        if (masterFile == null) {
            return 4;
        }
        if (!masterFile.MasterId.equals(loggedInUser.getUser().getId())) {
            return 1;
        }
        if (targetUserFile != null) {
            return 5;
        }

        bbc = new BigBrainCipher(new PBEFactory());
        String privateKey;
        try {
            privateKey = bbc.getFactory().decrypt(userPassword, loggedInUser.getUser().getPrivateKey().getBytes());
        } catch (Exception e) {
            userPassword = null;
            return 3;
        }
        userPassword = null;
        bbc = new BigBrainCipher(new RSAFactory());
        String aesKey;
        try {
            aesKey = bbc.getFactory().decrypt(privateKey, masterFile.AESkey.getBytes());
        } catch (Exception e) {
            privateKey = null;
            return 1;
        }
        privateKey = null;

        DataFile newUserForFile = new DataFile();
        String publicKeyOfUserToGiveAccessTo = null;
        try {
            publicKeyOfUserToGiveAccessTo = client.getPublicKey(user);
        } catch (SQLException e) {
            return 4;
        }

        if (publicKeyOfUserToGiveAccessTo == null) {
            return 2;
        }

        try {
            newUserForFile.AESkey = bbc.getFactory().encrypt(publicKeyOfUserToGiveAccessTo, aesKey.getBytes());
        } catch (Exception e) {
            return 4;
        }
        aesKey = null;
        try {
            newUserForFile.name = bbc.getFactory().encrypt(publicKeyOfUserToGiveAccessTo, fileName.getBytes());
        } catch (Exception e) {
            return 4;
        }
        newUserForFile.hashname = hashName;
        newUserForFile.MasterId = loggedInUser.getUser().getId();
        newUserForFile.SlaveId = user.getId();

        try {
            client.insertNewDataAccess(newUserForFile);
        } catch (SQLException e) {

            return 4;
        }
        return 0;
    }

    /**
     * 
     * 
     * 
     * @param user
     * @param fileName
     * @return 0 wenn alles geklappt hat, 1 wenn der Nutzer nicht der Master der
     *         Datei ist, 2 wenn der Nutzer dem man die Berechtigung nehmen will
     *         nicht existiert,3 wenn die Datei nicht existiert
     */
    public int removeUserFromFile(User user, String fileName) {
        // before decrypting sensitive data make sure the user is allowed to add others
        // to this file

        BigBrainCipher hashMaster = new BigBrainCipher();
        String hashName = hashMaster.hashForFiles(fileName);
        DataFile masterFile = new DataFile();
        try {
            masterFile = client.getDataAccess(hashName, loggedInUser.getUser().getId());
        } catch (SQLException e) {

            return 3;
        }
        if (masterFile == null) {
            return 3;
        }
        if (!masterFile.MasterId.equals(loggedInUser.getUser().getId())) {
            return 1;
        }

        int amountDeleted = 0;
        try {
            amountDeleted = client.removeDataAccess(user, hashName);
        } catch (SQLException e) {

            return 2;
        }
        if (amountDeleted < 1) {
            return 2;
        } else {
            return 0;
        }
    }
}

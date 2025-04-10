package de.hma.srn.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.hma.srn.domain.BigBrainCipher.AESFactory;
import de.hma.srn.domain.BigBrainCipher.BigBrainCipher;
import de.hma.srn.domain.BigBrainCipher.RSAFactory;
import de.hma.srn.persistence.DbClient;

public class FileEncrypter {

    private BigBrainCipher bbc;
    private String encryptedPath = "files/encryptedFiles/";
    private String decryptedPath = "files/decryptedFiles/";
    private FilePermissionManager filePermissionManager;
    private Boolean usesNotar = false;
    private String osName;

    public FileEncrypter() {
        bbc = new BigBrainCipher(new AESFactory());
        File decryptOrdner = new File(decryptedPath);
        File encryptedOrdner = new File(encryptedPath);
        filePermissionManager = new FilePermissionManager();
        if (!decryptOrdner.exists()) {
            decryptOrdner.mkdirs();
        }
        if (!encryptedOrdner.exists()) {
            encryptedOrdner.mkdirs();
        }
    }

    public void encryptFile(String keyString, String filepath)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SQLException,
            Exception {
        BigBrainCipher hashFactory = new BigBrainCipher();
        SecretKey key = AESFactory.convertStringToSecretKey(keyString);
        byte[] metaData = prepareMetaData(filepath, keyString);
        keyString = null;
        String outputFile = encryptedPath + hashFactory.hashForFiles(filepath);
        FileInputStream fis = new FileInputStream(filepath);
        FileOutputStream fos = new FileOutputStream(outputFile);

        byte[] iv = Arrays.copyOfRange(metaData, 0, 16);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        fos.write(metaData);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            byte[] encrypted = cipher.update(buffer, 0, bytesRead);
            fos.write(encrypted);
        }

        byte[] encrypted = cipher.doFinal();
        fos.write(encrypted);
        filePermissionManager.copyPermissions(filepath, outputFile);

        fis.close();
        fos.close();
    }

    public void decryptFile(String keyString, String filePath, String ablageOrt)
            throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        BigBrainCipher hashFactory = new BigBrainCipher();
        String inputFile = encryptedPath + hashFactory.hashForFiles(filePath);

        File decyptedFile = new File(decryptedPath + extractFileName(filePath));
        decyptedFile.createNewFile();

        String outputFile = ablageOrt + extractFileName(filePath);
        SecretKey key = AESFactory.convertStringToSecretKey(keyString);
        FileInputStream fis = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream(outputFile);

        byte[] metaData = readMetaData(inputFile);
        byte[] iv = Arrays.copyOfRange(metaData, 0, 16);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        key = null;
        byte[] buffer = new byte[1024];
        int bytesRead;
        fis.read(new byte[metaData.length]);
        while ((bytesRead = fis.read(buffer)) != -1) {
            byte[] decrypted = cipher.update(buffer, 0, bytesRead);
            fos.write(decrypted);
        }

        byte[] decrypted = cipher.doFinal();
        fos.write(decrypted);
        fos.close();
        fis.close();
        key = null;
        keyString = null;
        filePermissionManager.copyPermissions(inputFile, decryptedPath + extractFileName(filePath));
    }

    public String extractFileName(String path) {
        String[] readPaths = path.split("/");
        String[] readPathsWindows = path.split("\\\\");

        if (readPaths.length > 1) {

            return readPaths[readPaths.length - 1];

        } else {
            // if (readPathsWindows.length > 1) {
            return readPathsWindows[readPathsWindows.length - 1];

            // }
        }
    }

    public void deleteFile(String dateiName) {

        String encryptedFilePath = "files/encryptedFiles/" + dateiName;
        DbClient dbClient = DbClient.getInstance();
        ArrayList<DataFile> dataAccesses = new ArrayList<>();
        try {
            dataAccesses = dbClient.getDataAccessOf1File(dateiName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataAccesses.size() == 0) {
            File datei = new File(encryptedFilePath);
            datei.delete();
        }

    }

    /*
     * only for testing purposes
     */
    public String getNotarEncryptedKey(String filepath) throws IOException {
        FileInputStream fis = new FileInputStream(filepath);
        byte[] ivmüll = new byte[16];
        fis.read(ivmüll);
        byte[] notarKey = new byte[256];
        fis.read(notarKey);
        fis.close();
        String baseNotarKey = Base64.getEncoder().encodeToString(notarKey);
        return baseNotarKey;
    }

    /**
     * 
     * @param sourceFilePath
     * @param AESKeyString
     * @return byte Array with iv = 0-15, permissionBytes = 16-17, notarKey = 18-273
     * @throws IOException
     * @throws SQLException
     * 
     */
    private byte[] prepareMetaData(String sourceFilePath, String AESKeyString)
            throws IOException, SQLException, InvalidKeyException, Exception {
        byte[] iv = new byte[16];

        ByteArrayOutputStream metaDataOutputStream = new ByteArrayOutputStream();

        new SecureRandom().nextBytes(iv);
        metaDataOutputStream.write(iv);
        byte[] notarKey = new byte[512]; // because of 4096 byte NotarKey

        if (usesNotar) {
            String fileName = extractFileName(sourceFilePath);

            BigBrainCipher cipher = new BigBrainCipher(new RSAFactory());
            DbClient dbClient = DbClient.getInstance();
            String notarPublicKey = dbClient.getPublicKey(new User("notar", "0000"));
            String dataToEncryt = AESKeyString + " " + fileName + " ";
            String notarEncryptedKeyBase = cipher.getFactory().encrypt(notarPublicKey, dataToEncryt.getBytes());
            notarKey = Base64.getDecoder().decode(notarEncryptedKeyBase);
        }
        metaDataOutputStream.write(notarKey);
        metaDataOutputStream.close();

        return metaDataOutputStream.toByteArray();
    }

    /**
     * 
     * @param filePath
     * @return byte Array with iv = 0-15, permissionBytes = 16-17, notarKey = 18-273
     * @throws IOException
     * 
     */

    private byte[] readMetaData(String filePath) throws IOException {
        byte[] iv = new byte[16];

        ByteArrayOutputStream metaDataOutputStream = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(filePath);
        fis.read(iv);
        metaDataOutputStream.write(iv);

        byte[] notarInfo = new byte[512]; // because of 4096 RSA Notar key
        fis.read(notarInfo);
        metaDataOutputStream.write(notarInfo);

        fis.close();
        metaDataOutputStream.close();
        return metaDataOutputStream.toByteArray();
    }

    public void setNotarReadInfo(Boolean usesNotar) {
        this.usesNotar = usesNotar;
    }

    public String readPemFile(File file) throws Exception {
        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

        String publicKeyPEM = key
                .replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s", "");

        return publicKeyPEM;
    }
}

package com.notar;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.notar.BigBrainCipher.AESFactory;

public class FileEncrypter {

    PosixFilePermissions permissions;
    FilePermissionManager filePermissionManager;
    Boolean usesNotar = false;

    public FileEncrypter() {
        filePermissionManager = new FilePermissionManager();
    }

    public void decryptFile(String keyString, String filePath, String fileName, String ablageOrt)
            throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        String outputFile = ablageOrt + fileName;
        SecretKey key = AESFactory.convertStringToSecretKey(keyString);
        FileInputStream fis = new FileInputStream(filePath);
        FileOutputStream fos = new FileOutputStream(outputFile);

        byte[] metaData = readMetaData(filePath);
        byte[] iv = Arrays.copyOfRange(metaData, 0, 16);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
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
        filePermissionManager.copyPermissions(filePath, outputFile);
        key = null;
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

    /*
     * only for testing purposes
     */
    public String getNotarEncryptedKey(String filepath) throws IOException {
        FileInputStream fis = new FileInputStream(filepath);
        byte[] ivmüll = new byte[16];
        fis.read(ivmüll);
        byte[] notarKey = new byte[512];
        fis.read(notarKey);
        fis.close();
        String baseNotarKey = Base64.getEncoder().encodeToString(notarKey);
        return baseNotarKey;
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

        if (usesNotar) {
            byte[] notarInfo = new byte[512];
            fis.read(notarInfo);
            metaDataOutputStream.write(notarInfo);
        }

        fis.close();
        return metaDataOutputStream.toByteArray();
    }

    public void setNotarReadInfo(Boolean usesNotar) {
        this.usesNotar = usesNotar;
    }
}
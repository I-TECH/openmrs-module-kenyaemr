package org.openmrs.module.kenyaemr.security;

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;

/**
 * Created by agnes on 9/8/14.
 */
public class Encryption {

    public static String encrypt(final String dataToEncrypt, final String initialVector, final String secretKey) {
        String encryptedData = null;
        try {
            // Initialize the cipher

            final Cipher cipher = MD5.initCipher(Cipher.ENCRYPT_MODE, initialVector, secretKey);
            // Encrypt the data
            final byte[] encryptedByteArray = cipher.doFinal(dataToEncrypt.getBytes());
            // Encode using Base64
            encryptedData = (new BASE64Encoder()).encode(encryptedByteArray);
        } catch (Exception e) {
            System.err.println("Problem encrypting the data");
            e.printStackTrace();
        }
        return encryptedData;
    }

    public static String outPut() {
        final String iv = "0123456789abcdef"; // This has to be 16 characters
        final String secretKey = "Replace this by your secret key";
        final MD5 encryption = new MD5();

        final String encryptedData = Encryption.encrypt("root", iv, secretKey);
        System.out.println("ati encrypted"+encryptedData);

        return encryptedData;
    }
}

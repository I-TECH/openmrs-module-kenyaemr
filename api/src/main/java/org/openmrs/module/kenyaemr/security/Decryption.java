package org.openmrs.module.kenyaemr.security;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;

/**
 * Created by agnes on 9/8/14.
 */
public class Decryption {

	public static String decrypt(final String encryptedData, final String initialVector, final String secretKey) {
		String decryptedData = null;
		try {
			// Initialize the cipher
			final Cipher cipher = MD5.initCipher(Cipher.DECRYPT_MODE, initialVector, secretKey);
			// Decode using Base64
			final byte[] encryptedByteArray = (new BASE64Decoder()).decodeBuffer(encryptedData);
			// Decrypt the data
			final byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
			decryptedData = new String(decryptedByteArray, "UTF8");
		} catch (Exception e) {
			System.err.println("Problem decrypting the data");
			e.printStackTrace();
		}
		return decryptedData;
	}

	public static String outPut() {
		final String iv = "0123456789abcdef"; // This has to be 16 characters
		final String secretKey = "Replace this by your secret key";
		final MD5 decryption = new MD5();

		final String decryptedDatas = Decryption.decrypt(Encryption.outPut(), iv, secretKey);
		System.out.println(decryptedDatas);
		return decryptedDatas;
	}
}

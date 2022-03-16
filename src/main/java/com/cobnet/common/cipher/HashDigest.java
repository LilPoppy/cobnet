package com.cobnet.common.cipher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashDigest {

	public static byte[] encrypt(String algorithm, String input) throws NoSuchAlgorithmException {
		
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		
		return digest.digest(input.getBytes());
	}
}

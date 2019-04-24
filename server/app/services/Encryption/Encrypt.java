//package services;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.io.IOException;

public class Encrypt {
	public static byte[] encrypt(String toEncrypt) throws EncryptionException {
		try {
			return Decrypt.getAES(Decrypt.getKey()).encrypt(toEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EncryptionException("error during encryption");
		}
	}
}

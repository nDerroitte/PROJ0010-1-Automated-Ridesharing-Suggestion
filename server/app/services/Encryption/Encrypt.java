package services;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.io.IOException;

public class Encrypt {
	public static String encrypt(String toEncrypt) throws EncryptionException {
		try {
			return getAES(getKey()).encrypt(toEncrypt);
		} catch (Exception e) {
			throw new EncryptionException("error during encryption");
		}
	}
}

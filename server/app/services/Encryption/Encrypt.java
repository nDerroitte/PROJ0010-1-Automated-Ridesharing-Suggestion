package services;

import java.io.IOException;
import java.util.ArrayList;

public class Encrypt {
	public static ArrayList<Byte> encrypt(String toEncrypt) throws EncryptionException {
		try {
			return Decrypt.getAES(Decrypt.getKey()).encrypt(toEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EncryptionException("Error during encryption");
		}
	}
}

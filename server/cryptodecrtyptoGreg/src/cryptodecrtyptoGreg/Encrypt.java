package cryptodecrtyptoGreg;

import java.io.IOException;

public class Encrypt {
	/*
	 * This function takes in argument a String and an encrypter and returns the encrypted String.
	 */
	public static String encryption(String toencrypt, AES encrypter) throws IOException, EncryptionException{
        return encrypter.encrypt(toencrypt);
	}

}

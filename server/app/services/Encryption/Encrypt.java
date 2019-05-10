package services;

import java.io.IOException;
import java.util.ArrayList;
import java.security.KeyStore;
import javax.crypto.SecretKey;
public class Encrypt {
	private static SecretKey key;
	public static ArrayList<Byte> encrypt(String toEncrypt) throws EncryptionException {
		try {	
			char[] password = "9dh4gkd8".toCharArray();
			KeyStore ks = Keystore.createKeystore();
			SecretKey keysecret = (SecretKey) Keystore.gettingKey(ks, "keyAlias1", password);
			setKey(keysecret);
			return getAES().encrypt(toEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EncryptionException("Error during encryption");
		}
	}
	public static AES getAES() throws Exception {
        return new AES(getKey());
}
	static void setKey(SecretKey keysecret){
        key = keysecret;
    }
    static SecretKey getKey(){
        return key;
    }
	
}

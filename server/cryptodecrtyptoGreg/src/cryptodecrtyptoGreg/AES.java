package cryptodecrtyptoGreg;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.NoSuchPaddingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

public class AES {

     private Cipher ecipher;
     private Cipher dcipher;
     
     public AES(SecretKey key) throws EncryptionException {
    	try {
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
        	throw new EncryptionException("Invalid key in initialisation");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
        	throw new EncryptionException("Using invalid or not implemented algorithm in Cipher getInstance method");
        }
    }

    /*
	 * This function takes in argument a String and returns a corresponding encrypted String.
	 */
    public String encrypt(String str) throws UnsupportedEncodingException, EncryptionException {
        try {
            byte[] utf8 = str.getBytes("UTF-8");
            byte[] enc = ecipher.doFinal(utf8);
             
            return new String(Base64.getEncoder().encode(enc), "UTF-8");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        	throw new EncryptionException("Error in doFinal : decrypting with encryption cipher or vice-versa");
        }
    }

    public String decrypt(String str) throws IOException, EncryptionException {
        try {
            byte[] dec = new String(Base64.getDecoder().decode(str)).getBytes();
            byte[] utf8 = dcipher.doFinal(dec);
            return new String(utf8, "UTF-8");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        	throw new EncryptionException("Error in doFinal : decrypting with encryption cipher or vice-versa");
        }
    }
}
//package services;

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
            int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
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
    public byte[] encrypt(String str) throws UnsupportedEncodingException, EncryptionException {
        try {
            StringBuilder SB = new StringBuilder();
			SB.append(str);
			while(SB.length()%16!=0){
				SB.append("0");
			}
			str = SB.toString();
            byte[] returnB = str.getBytes("UTF-8");
            byte[] encry = ecipher.doFinal(returnB);
            return encry;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        	throw new EncryptionException("Error in doFinal: " + e.getMessage());
        }
    }

    public String decrypt(byte[] byteArray) throws IOException, EncryptionException {
        try {
            if(byteArray.length%16!=0){
                return "bad encryption sequence";
            }
            byte[] dcrypt = dcipher.doFinal(byteArray);
            return new String(dcrypt, "UTF-8");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        	throw new EncryptionException("Error in doFinal: " + e.getMessage());
        }
    }
}
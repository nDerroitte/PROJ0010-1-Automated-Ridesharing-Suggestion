package services;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.NoSuchPaddingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;
import java.util.ArrayList;

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

    /**
     * Encrypts the argument.
     *
     * @param str the string to encrypt.
     * @return an ArrayList<Byte> containing the bytes of the corresponding encrypted string
     */
    public ArrayList<Byte> encrypt(String str) throws UnsupportedEncodingException, EncryptionException {
        try {
            StringBuilder SB = new StringBuilder();
            SB.append(str);
            if (str.length()%16 == 0) {
                if (str.substring(str.length() - 1).equals("0")) {
                    for(int i = 0; i < 16; i++) {
                        SB.append("1");
                    }
                } else {
                    for(int i = 0; i < 16; i++) {
                        SB.append("0");
                    }
                }
            } else {
                int padding = 16 - SB.length() % 16;
                if (str.substring(str.length() - 1).equals("0")) {
                    for(int j = 0; j < padding; j++) {
                        SB.append("1");
                    }
                } else {
                    for(int j = 0; j < padding; j++) {
                        SB.append("0");
                    }
                }
            }
            str = SB.toString();
            byte[] returnB = str.getBytes("UTF-8");
            byte[] encry = ecipher.doFinal(returnB);

            ArrayList<Byte> returnArrayList = new ArrayList<Byte>();
            for (byte b : encry) {
                returnArrayList.add(new Byte(b));
            }
            return returnArrayList;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Error in doFinal: " + e.getMessage());
        }
    }

    /**
     * Decrypts the argument.
     * It is guarenteed that decrypt(encrypt(str)).equals(str) is always True.
     *
     * @param arrayList the bytes of the encrypted string.
     *
     * @return the decrypted String
     */
    public String decrypt(ArrayList<Byte> arrayList) throws IOException, EncryptionException {
        try {
            byte[] byteArray = new byte[arrayList.size()];
            int i = 0;
            // Number because runtype type is Integer
            for (Number b : arrayList) {
                byteArray[i] = b.byteValue();
                i++;
            }

            if(arrayList.size()%16!=0){
                throw new EncryptionException("Argument should have a multiple of 16 as length.");
            }
            byte[] dcrypt = dcipher.doFinal(byteArray);
            String returned = new String(dcrypt, "UTF-8");
            StringBuilder SB = new StringBuilder();
            SB.append(returned);
            int initLength = SB.length();
            if(SB.substring(SB.length()-1).equals("0")){
                while(SB.substring(SB.length()-1).equals("0")){
                    SB.setLength(--initLength);
                }
            }else if (returned.substring(returned.length()-1).equals("1")){
                while(SB.substring(SB.length()-1).equals("1")){
                    SB.setLength(--initLength);
                }
            }

            return SB.toString();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Error in doFinal: " + e.getMessage());
        }
    }
}
package services;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


public class Decrypt {
    public static String decrypt(ArrayList<Byte> toDecrypt) throws EncryptionException {
        try {
            return decrypt(toDecrypt, getKey());
        } catch (Exception e) {
            throw new EncryptionException("Error during decryption");
        }
    }

    private static String decrypt(ArrayList<Byte> toDecrypt, String key) throws Exception {
        AES aes = getAES(key);
        return aes.decrypt(toDecrypt);
    }

    static AES getAES(String myKey) throws Exception {
        byte[] salt = myKey.getBytes();
        char[] password = "9dh4dkg8".toCharArray();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");
        return new AES(key);
    }

    static String getKey() throws Exception { //throws FileNotFoundException
        //File file=new File("k2.txt");
        //System.out.println("FILE EXIST : "+file.exists());
        //String k1 = readFile("k2.txt");
        //String k2 = readFile("k3.txt");
        //Keystore.createKeystore();

        String k1 = "3b924dc42d627a7113078580f8851f3594caa6fa83af0a4782e28271e43ea2c3a5a7ab514e899e0cfc7481fc08f40ddf54dad2dd8d3d83cb23a48b31d79fac05";
        //String k1xork2 = xor(Constants.k2, Constants.k3);
        //String k3 = readFile("k6.txt");
        //String ke = xor(k1xork2, Constants.k6);
        //AES aes = getAES(ke); 
        //String k4 = Decrypt.decrypt(readFile("k7.txt"), ke);
        return k1;
    }
}

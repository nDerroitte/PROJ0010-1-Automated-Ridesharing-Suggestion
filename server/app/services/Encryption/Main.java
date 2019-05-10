package services;

import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.util.Base64;
import java.io.FileOutputStream; 
import java.util.ArrayList;
import java.nio.file.Files; 
public class Main {
	
	public static void main(String[] args) throws Exception { 
        try{
        ArrayList<Byte> k4Encrypted = Encrypt.encrypt("Coucou");
        System.out.println(Decrypt.decrypt(k4Encrypted));
        /*FileOutputStream fos = new FileOutputStream("text8.txt");
        fos.write(k4Encrypted);
        byte[] array = Files.readAllBytes(new File("text8.txt").toPath());
        String decrypted = Decrypt.decrypt(array);
        System.out.println(decrypted);*/
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("error during main");
        }
    }
}

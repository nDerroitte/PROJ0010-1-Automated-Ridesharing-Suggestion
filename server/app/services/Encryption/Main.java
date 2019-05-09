package services;

import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.util.Base64;
import java.io.FileOutputStream; 

import java.nio.file.Files; 
public class Main {
	
	public static void main(String[] args) throws Exception { 
        try{
            Keystore.createKeystore();
       /* ArrayList<Byte> k4Encrypted = Encrypt.encrypt("87f0056b10cb51e87b8b41388e4152096533750ca4b63870ccdf05b85b3d81e3329764092a743378003cbcc070c2abd3e95d2fa006427db87197c75554e5a3ed");
        FileOutputStream fos = new FileOutputStream("text8.txt");
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

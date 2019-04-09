package services;

import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.util.Base64;

public class Main {
	
	public static void main(String[] args) throws Exception { //will never be used in prod

		byte[] salt= "bf2dfa595fcc80bd07ce8205717e049438314c19ff92f60ffabe6d6a22909aa6d9485dca67592d32359e3ab8dd308b9999a15587e770fffcfec33b3680420375".getBytes(); //args[0];
		char[] password= "passwd".toCharArray(); //args[1];

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
		
        //String mykey ="1234567891011121"; // Ke
        //SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES");

        AES encrypter=new AES(key);
        String k4Encrypted = encrypter.encrypt("87f0056b10cb51e87b8b41388e4152096533750ca4b63870ccdf05b85b3d81e3329764092a743378003cbcc070c2abd3e95d2fa006427db87197c75554e5a3ed");
        System.out.print(k4Encrypted);
        }

}

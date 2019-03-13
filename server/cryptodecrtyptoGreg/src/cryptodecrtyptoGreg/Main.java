package cryptodecrtyptoGreg;

import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class Main {
	
	public static void main(String[] args) throws Exception { //will never be used in prod
		
		String username= "Coco"; //args[0];
		String password= "passwd"; //args[1];
		String coordinate= "12.12.12.12";//args[3];
		
		
        String mykey ="1234567891011121"; // Ke
        SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES");

        AES encrypter=new AES(key);
        String k4Encrypted = encrypter.encrypt("87f0056b10cb51e87b8b41388e4152096533750ca4b63870ccdf05b85b3d81e3329764092a743378003cbcc070c2abd3e95d2fa006427db87197c75554e5a3ed");
        System.out.print(k4Encrypted);
        }

}

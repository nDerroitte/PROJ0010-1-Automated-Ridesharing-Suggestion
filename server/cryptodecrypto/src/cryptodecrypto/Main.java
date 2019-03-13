package cryptodecrypto;
import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class Main {
	
	public static void main(String[] args) throws IOException  {
		
		String username= "Coco"; //args[0];
		String password= "passwd"; //args[1];
		String coordinate= "12.12.12.12";//args[3];
		
		
        String mykey ="1234567891234567";
        SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES");

        AES encrypter=new AES(key);
        File file = new File("Hello1.txt");
        // creates the file
        file.createNewFile();
        
        Encrypt.encryption(username, password, file, encrypter );
        
        Decrypt.translate(file, encrypter);
        
        }

}

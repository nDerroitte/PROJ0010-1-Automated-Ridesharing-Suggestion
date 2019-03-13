package cryptodecrypto;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;

public class Encrypt {
	public static void encryption(String username, String password, File file, AES encrypter) throws IOException{

		String toencrypt=username+" "+password;
        System.out.println("Original String :"+toencrypt);

        System.out.println("Encrypted data :"+encrypter.encrypt(toencrypt));
      
        
        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file); 
        
        // Writes the content to the file
        writer.write(encrypter.encrypt(toencrypt)); 
        writer.flush();
        writer.close();
	}

}

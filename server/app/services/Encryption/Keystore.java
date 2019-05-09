package services;

import java.security.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.*;

public class Keystore{
    public static void createKeystore() throws EncryptionException{
        try{
            //Loading the KeyStore
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            char[] keyStorePassword = "9dh4gkd8".toCharArray();
            //InputStream keyStoreData = new FileInputStream("keystore.ks");
            keyStore.load(null, keyStorePassword);
            
            //Set key 
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyStorePassword);
            
            keyStore.setEntry("keyAlias1", secretKeyEntry, entryPassword);

            //Getting Keys
            
            KeyStore.Entry keyEntry = keyStore.getEntry("keyAlias1", entryPassword);
            SecretKey key = (SecretKey) keyStore.getKey("keyAlias1", keyStorePassword);
            //String key_str = key.toSting();
            String key_str = Base64.getEncoder().encodeToString(key.getEncoded());
            System.out.println(key_str);
            //Storing the Keystore 
            FileOutputStream keyStoreOutputStream = new FileOutputStream("keystore.ks");
            keyStore.store(keyStoreOutputStream, keyStorePassword);
        }
        catch(KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException | IOException  e){
            e.printStackTrace();
            throw new EncryptionException("Error during the creation of the keystore.");
        }
    
    }
}
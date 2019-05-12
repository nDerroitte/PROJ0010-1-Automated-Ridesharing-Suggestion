package services;

import java.security.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.*;
import java.security.spec.InvalidKeySpecException;

public class Keystore{

    public static KeyStore createKeystore() throws EncryptionException{
        try{
            //Loading the KeyStore
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            char[] password = "9dh4gkd8".toCharArray();
            keyStore.load(null, password);
            
            //Set key 
            //Create key 
            String salt_str = "ULiège";
            byte[] salt = salt_str.getBytes();
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(password);
            
            keyStore.setEntry("keyAlias1", secretKeyEntry, entryPassword);
           
            //Storing the Keystore 
            //FileOutputStream keyStoreOutputStream = new FileOutputStream("keystore.ks");
            //keyStore.store(keyStoreOutputStream, keyStorePassword);
            return keyStore;
        }
        catch(KeyStoreException | NoSuchAlgorithmException | CertificateException  | InvalidKeySpecException | IOException  e){
            e.printStackTrace();
            throw new EncryptionException("Error during the creation of the keystore.");
        }
    }

    public static SecretKey gettingKey(KeyStore ks, String keyAlias, char[] keyStorePassword) throws KeyStoreException{
        try {
            SecretKey key = (SecretKey) ks.getKey(keyAlias, keyStorePassword);
            return key;
        }catch(KeyStoreException | NoSuchAlgorithmException  |UnrecoverableKeyException e){
            e.printStackTrace();
            throw new KeyStoreException("Error to get the key from the keystore.");
        }
        
    }
}
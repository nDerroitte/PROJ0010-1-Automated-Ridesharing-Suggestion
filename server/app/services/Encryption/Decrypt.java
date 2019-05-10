package services;

import java.io.IOException;
import java.util.ArrayList;

public class Decrypt {

    public static String decrypt(ArrayList<Byte> toDecrypt) throws EncryptionException {
        try {
            AES aes = Encrypt.getAES();
            return aes.decrypt(toDecrypt);
        } catch (Exception e) {
            throw new EncryptionException("Error during decryption");
        }
    }
}

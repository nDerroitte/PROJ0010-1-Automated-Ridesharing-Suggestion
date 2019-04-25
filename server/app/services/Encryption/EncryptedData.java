package services;

import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.util.Base64;
import java.io.FileOutputStream; 

import java.nio.file.Files; 

public class EncryptedData{
    private byte[] data;
    private int initLength;

    public EncryptedData(byte[] data, int initLength){
        this.data = data;
        this.initLength = initLength;
    }
    
    public byte[] getData(){
        return data;
    }

    public int getInitLength(){
        return initLength;
    }
}
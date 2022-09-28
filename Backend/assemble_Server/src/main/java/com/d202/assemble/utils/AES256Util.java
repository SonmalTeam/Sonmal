package com.d202.assemble.utils;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AES256Util {
    //128bit
    //비밀키의 키 값 = 이건 노출되면 안되는 정보
    //@Value("${external.secret.key}")
    private String KEY="RgUkXp2s5v8x/A?D";
    private static final int IVSIZE = 16;//128bit = 16bytes

    public String encrypt(String plainText) throws Exception{
        SecretKey keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");//비밀키
        //1. Initialization Vector 생성
        byte[] iv = new byte[IVSIZE];//초기값 (CBC모드는 이전 값과 XOR하는 암호화라서 초기값이 하나 있어야됨)
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);//크기에 맞는 초기값 생성
        AlgorithmParameterSpec ivspec = new IvParameterSpec(iv);

        //2. Encryption
        //랜덤 IV값을 쓰기 때문에, 값 앞에 IV값을 붙여서 써야함.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//16바이트 블럭으로 나누다가 마지막 블럭이 딱 나눠떨어지지 않으면 padding채움
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivspec);
        byte[] plainTextBytes = plainText.getBytes("UTF-8");

        byte[] cipherBytes = cipher.doFinal(plainTextBytes);
        //add iv
        byte[] result = new byte[IVSIZE+cipherBytes.length];
        System.arraycopy(iv, 0, result, 0, IVSIZE);
        System.arraycopy(cipherBytes, 0, result, IVSIZE, cipherBytes.length);
        return new String(Base64.getEncoder().encode(result), "UTF-8");
    }

    public String decrypt(String encryptedIvText) throws Exception{
        byte[] encryptedIvTextBytes = Base64.getDecoder().decode(encryptedIvText);

        //1. IV값 분리
        byte[] iv = new byte[IVSIZE];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, IVSIZE);
        int encryptedSize = encryptedIvTextBytes.length-IVSIZE;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, IVSIZE, encryptedBytes, 0, encryptedSize);

        //2. Decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey keyspec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        AlgorithmParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] aesDecode = cipher.doFinal(encryptedBytes);

        return new String(aesDecode, "UTF-8");
    }
}

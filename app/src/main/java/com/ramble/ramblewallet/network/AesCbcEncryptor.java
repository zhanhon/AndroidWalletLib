package com.ramble.ramblewallet.network;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 时间　: 2021/12/21 14:12
 * 作者　: potato
 * 描述　:
 */
public class AesCbcEncryptor {
    /**
     * 全局数组
     **/
    private final static String[] HEX_STR_ARRAY = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
    /**
     * charset
     */
    private static final String CHARSET = "UTF-8";
    /**
     * Secret type
     **/
    private static final String AES_TYPE = "AES/CBC/PKCS5Padding";
    /**
     * 16位iv
     */
    private static final String ivParameter = "1234567890123456";

    private static AesCbcEncryptor instance = null;

    public static AesCbcEncryptor getInstance() {
        if (instance == null) {
            synchronized (AesCbcEncryptor.class) {
                if (instance == null) {
                    instance = new AesCbcEncryptor();
                }
            }
        }
        return instance;
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param bytes
     * @return
     */
    private static String byteToHexStr(byte[] bytes) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sBuffer.append(byteToArrayString(bytes[i]));
        }
        return sBuffer.toString();
    }

    private static String byteToArrayString(byte bt) {
        int iRet = bt;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return HEX_STR_ARRAY[iD1] + HEX_STR_ARRAY[iD2];
    }

    public String generateKey(int bit) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(bit);
        SecretKey key = keygen.generateKey();
        byte[] encoded = key.getEncoded();
        return byteToHexStr(encoded);
    }

    public String encrypt(String input, String key) throws Exception {
        try {
            byte[] inputBytes = input.getBytes(CHARSET);
            byte[] output = encrypt(inputBytes, key.getBytes(CHARSET));
            return new String(Base64.encode(output, Base64.NO_WRAP), CHARSET);
        } catch (Exception e) {
            throw e;
        }
    }

    public String encrypt(byte[] input, String key) throws Exception {
        try {
            byte[] inputBytes = input;
            byte[] output = encrypt(inputBytes, key.getBytes(CHARSET));
            return new String(Base64.encode(output, Base64.NO_WRAP), CHARSET);
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] encrypt(byte[] inputBytes, byte[] keyBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_TYPE);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(CHARSET));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        return cipher.doFinal(inputBytes);
    }

    public String decrypt(String input, String key) throws Exception {
        try {
            byte[] inputBytes = Base64.decode(input.getBytes(CHARSET), Base64.NO_WRAP);
            byte[] output = decrypt(inputBytes, key.getBytes(CHARSET));
            return new String(output, CHARSET);
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] decryptByte(String input, String key) throws Exception {
        try {
            byte[] inputBytes = Base64.decode(input.getBytes(CHARSET), Base64.NO_WRAP);
            byte[] output = decrypt(inputBytes, key.getBytes(CHARSET));
            return output;
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] decrypt(byte[] inputBytes, byte[] keyBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_TYPE);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(CHARSET));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        return cipher.doFinal(inputBytes);
    }
}
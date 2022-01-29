package com.ramble.ramblewallet.tronsdk.common.crypto;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SymmEncoder {


    public static SecretKey restoreSecretKey(byte[] secretBytes, String algorithm) {
        SecretKey secretKey = new SecretKeySpec(secretBytes, algorithm);
        return secretKey;
    }

    public static byte[] AES128EcbEnc(byte[] plain, byte[] aesKey) {
        if (aesKey == null || aesKey.length != 16) {
            return null;
        }
        if (plain == null || (plain.length & 0x0F) != 0) {
            return null;
        }

        SecretKey key = restoreSecretKey(aesKey, "AES");
        return AesEcbEncode(plain, key);
    }

    public static byte[] AES128EcbDec(byte[] encoded, byte[] aesKey) {
        if (aesKey == null || aesKey.length != 16) {
            return null;
        }
        if (encoded == null || (encoded.length & 0x0F) != 0) {
            return null;
        }

        SecretKey key = restoreSecretKey(aesKey, "AES");
        return AesEcbDecode(encoded, key);
    }


    private static byte[] AesEcbEncode(byte[] plainText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static byte[] AesEcbDecode(byte[] encodedText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encodedText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] AESEcbEnc(byte[] plain, byte[] aesKey) {
        if (aesKey == null || aesKey.length != 16) {
            return null;
        }
        if (plain == null) {
            return null;
        }

        SecretKey key = restoreSecretKey(aesKey, "AES");
        return AesEcbPKCS7Encode(plain, key);
    }

    public static byte[] AESEcbDec(byte[] encoded, byte[] aesKey) {
        if (aesKey == null || aesKey.length != 16) {
            return null;
        }
        if (encoded == null) {
            return null;
        }

        SecretKey key = restoreSecretKey(aesKey, "AES");
        return AesEcbPKCS7Decode(encoded, key);
    }

    private static byte[] AesEcbPKCS7Encode(byte[] plainText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static byte[] AesEcbPKCS7Decode(byte[] encodedText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encodedText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
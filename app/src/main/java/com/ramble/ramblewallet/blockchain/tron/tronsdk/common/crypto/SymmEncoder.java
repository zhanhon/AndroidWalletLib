package com.ramble.ramblewallet.blockchain.tron.tronsdk.common.crypto;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SymmEncoder {

    private SymmEncoder() {
        throw new IllegalStateException("SymmEncoder");
    }

    public static SecretKey restoreSecretKey(byte[] secretBytes, String algorithm) {
        SecretKey secretKey = new SecretKeySpec(secretBytes, algorithm);
        return secretKey;
    }

    public static byte[] AES128EcbDec(byte[] encoded, byte[] aesKey) {
        if (aesKey == null || aesKey.length != 16) {
            return new byte[0];
        }
        if (encoded == null || (encoded.length & 0x0F) != 0) {
            return new byte[0];
        }

        SecretKey key = restoreSecretKey(aesKey, "AES");
        return AesEcbDecode(encoded, key);
    }

    private static byte[] AesEcbDecode(byte[] encodedText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encodedText);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

}
package com.ramble.ramblewallet.network;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 时间　: 2021/12/21 14:15
 * 作者　: potato
 * 描述　:
 */
public class RsaEncryptor {

    /**
     * Public Key
     */
    private final RSAPublicKey publicKey;

    public RsaEncryptor(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * 加载 公钥
     *
     * @param publicKeyStr
     * @return
     * @throws
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        // BASE64Decoder base64Decoder = new BASE64Decoder();
        // byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
        byte[] buffer = Base64.decode(publicKeyStr, Base64.NO_WRAP);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * 去掉换行符（\n）
     *
     * @param src
     * @return
     */
    private String replaceLine(String src) {
        return src.replace("\n", "");
    }

    public String encryptedByPublicKey(String plainText) throws Exception {
        byte[] binaryData = encryptedByPublicKey(plainText.getBytes());
        // String base64String = new BASE64Encoder().encodeBuffer(binaryData);
        String base64String = Base64.encodeToString(binaryData, Base64.NO_WRAP);
        return replaceLine(base64String);
    }

    public byte[] encryptedByPublicKey(byte[] plainTextData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        return cipher.doFinal(plainTextData);
    }

    public String decryptByPublicKey(String cipherText) throws Exception {
        // byte[] cipherData = new BASE64Decoder().decodeBuffer(cipherText);
        byte[] cipherData = Base64.decode(cipherText, Base64.NO_WRAP);
        byte[] binaryData = decryptByPublicKey(cipherData);
        return new String(binaryData);
    }

    public byte[] decryptByPublicKey(byte[] cipherData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
        return cipher.doFinal(cipherData);
    }
}

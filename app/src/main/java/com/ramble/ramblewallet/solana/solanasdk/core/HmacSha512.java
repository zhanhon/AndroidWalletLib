package com.ramble.ramblewallet.solana.solanasdk.core;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for Hmac SHA-512
 */
public class HmacSha512 {

    private static final String HMAC_SHA512 = "HmacSHA512";

    /**
     * hmac512
     *
     * @param key  key
     * @param seed seed
     * @return hmac512
     */
    public static byte[] hmac512(byte[] key, byte[] seed) {
        try {
            Mac sha512_HMAC = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(seed, HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            return sha512_HMAC.doFinal(key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Unable to perform hmac512.", e);
        }
    }
}
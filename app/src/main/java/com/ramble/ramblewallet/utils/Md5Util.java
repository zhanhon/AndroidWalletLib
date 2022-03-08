package com.ramble.ramblewallet.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @创建人： Ricky
 * @创建时间： 2021/5/26
 */
public class Md5Util {

    private Md5Util() {
        throw new IllegalStateException("Md5Util");
    }

    public static String md5(String password) {
        String passwordMd5 = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(password.getBytes(StandardCharsets.UTF_8));
            passwordMd5 = toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordMd5;
    }

    public static String toHex(byte[] bytes) {

        final char[] hexDigits = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(hexDigits[(bytes[i] >> 4) & 0x0f]);
            ret.append(hexDigits[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }
}



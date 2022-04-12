package com.ramble.ramblewallet.blockchain.ethereum.utils;

public class StringHexUtils {

    private StringHexUtils() {
        throw new IllegalStateException("StringHexUtils");
    }

    /**
     * 转16进制编码
     *
     * @param byteArr
     * @return
     */
    public static String byte2HexString(byte[] byteArr) {
        if (byteArr == null || byteArr.length < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte t : byteArr) {
            if ((t & 0XF0) == 0) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(t & 0XFF));
        }
        return sb.toString();
    }

}

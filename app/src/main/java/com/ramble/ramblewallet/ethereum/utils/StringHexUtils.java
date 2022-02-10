package com.ramble.ramblewallet.ethereum.utils;

import org.web3j.utils.Numeric;

public class StringHexUtils {

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

    /**
     * 十六进制解码
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStr2Bytes(String hexStr) {
        if ("0x".equals(hexStr.substring(0, 2))) {
            hexStr = hexStr.substring(2);
        }
        if (hexStr == null || hexStr.length() < 1) {
            return null;
        }
        int byteLen = hexStr.length() / 2;
        byte[] result = new byte[byteLen];
        char[] hexChar = hexStr.toCharArray();
        for (int i = 0; i < byteLen; i++) {
            result[i] = (byte) (Character.digit(hexChar[i * 2], 16) << 4 | Character.digit(hexChar[i * 2 + 1], 16));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        /*String remark = "0xa9059cbb";
        System.out.println(byte2HexString(remark.getBytes(StandardCharsets.UTF_8)));

        String str = "0xa9059cbb";
        String hex = new String(hexStr2Bytes(str), "UTF-8");
        System.out.println(hex);*/

        //private static final String TRANSFER_METHOD = "0xa9059cbb";
        //a9059cbb
        String transfer = "transfer(address,uint256)";
        byte[] bytes = transfer.getBytes();
        byte[] bytes1 = org.web3j.crypto.Hash.sha3(bytes);
        String method = Numeric.toHexString(bytes1, 0, 4, true);
        System.out.println(method);
    }

}

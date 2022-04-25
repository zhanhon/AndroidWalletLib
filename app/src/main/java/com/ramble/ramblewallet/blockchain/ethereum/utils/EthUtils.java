package com.ramble.ramblewallet.blockchain.ethereum.utils;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;

/**
 * @author Angus
 */
public class EthUtils {

    private EthUtils() {
        throw new IllegalStateException("EthUtils");
    }

    /**
     * 通过私钥获取地址
     *
     * @param ecKeyPair
     * @return
     */
    public static String getAddress(ECKeyPair ecKeyPair) {
        return replaceAddress(Keys.getAddress(ecKeyPair));
    }

    /**
     * 格式化地址
     *
     * @param address
     * @return
     */
    public static String replaceAddress(String address) {
        if (address != null && !address.startsWith("0x")) {
            return "0x" + address;
        }
        return address;
    }

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

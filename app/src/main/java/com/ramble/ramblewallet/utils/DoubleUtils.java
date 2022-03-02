package com.ramble.ramblewallet.utils;

/**
 * 时间　: 2022/3/1 13:27
 * 作者　: potato
 * 描述　:
 */
public class DoubleUtils {
    private static long lastClickTime;
    private final static long TIME = 500;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}

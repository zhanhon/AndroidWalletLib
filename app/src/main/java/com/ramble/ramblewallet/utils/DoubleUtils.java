package com.ramble.ramblewallet.utils;

/**
 * @创建人： Ricky
 * @创建时间： 2022/3/21
 */
public class DoubleUtils {
    private final static long TIME = 10000;
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}

package com.ramble.ramblewallet.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {

    private static final String SP_NAME = "xz";
    private static SharedPreferences sp;

    private SharedPreferencesUtils() {
        throw new IllegalStateException("SharedPreferencesUtils");
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).commit();

    }

    //接收信息ww
    //发送信息
    //删除信息
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, defValue);
    }


    public static void saveString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(key, defValue);
    }

}

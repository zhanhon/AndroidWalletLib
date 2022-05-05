package com.ramble.ramblewallet.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {

    private static final String SP_NAME = "xz";
    private static SharedPreferences sp;
    private static SecuritySharedPreference securitySP;

    private SharedPreferencesUtils() {
        throw new IllegalStateException("SharedPreferencesUtils");
    }

//    public static void saveSecurityBoolean(Context context, String key, boolean value) {
//        if (sp == null)
//            sp = context.getSharedPreferences(SP_NAME, 0);
//        sp.edit().putBoolean(key, value).commit();
//    }
//
//    public static boolean getSecurityBoolean(Context context, String key, boolean defValue) {
//        if (sp == null)
//            sp = context.getSharedPreferences(SP_NAME, 0);
//        return sp.getBoolean(key, defValue);
//    }
//
//    public static void saveSecurityString(Context context, String key, String value) {
//        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
//        sp.edit().putString(key, value).commit();
//    }
//
//    public static String getSecurityString(Context context, String key, String defValue) {
//        if (sp == null)
//            sp = context.getSharedPreferences(SP_NAME, 0);
//        return sp.getString(key, defValue);
//    }

    //////////////////////////////////////////AES加解密////////////////////////////////////////
    public static void saveSecurityBoolean(Context context, String key, boolean value) {
        securitySP = new SecuritySharedPreference(context, SP_NAME, Context.MODE_PRIVATE);
        securitySP.edit().putBoolean(key, value).apply();
    }

    public static boolean getSecurityBoolean(Context context, String key, boolean defValue) {
        if (securitySP == null)
            securitySP = new SecuritySharedPreference(context, SP_NAME, Context.MODE_PRIVATE);
        return securitySP.getBoolean(key, defValue);
    }

    public static void saveSecurityString(Context context, String key, String value) {
        securitySP = new SecuritySharedPreference(context, SP_NAME, Context.MODE_PRIVATE);
        securitySP.edit().putString(key, value).apply();
    }

    public static String getSecurityString(Context context, String key, String defValue) {
        if (securitySP == null)
            securitySP = new SecuritySharedPreference(context, SP_NAME, Context.MODE_PRIVATE);
        return securitySP.getString(key, defValue);
    }

}

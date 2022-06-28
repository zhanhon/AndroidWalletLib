package com.ramble.ramblewallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ramble.ramblewallet.MyApp;
import com.ramble.ramblewallet.constant.ConstantsKt;


public class SharedPreferencesUtils {

    private static final String SP_NAME = "xz";
    private static SecuritySharedPreference securitySP;

    private SharedPreferencesUtils() {
        throw new IllegalStateException("SharedPreferencesUtils");
    }

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

    //
    public static String getLanguage(){
        return getSecurityString(MyApp.Companion.getGetInstance().getSInstance(), ConstantsKt.LANGUAGE,ConstantsKt.CN);
    }

    /////////////////////////////宿主////////////////////////////////
    private static final String MODEL_KEY = "CLIENT_CONFIG";
    private static final String LANGUAGES_TYPE = "LANGUAGES_TYPE";;
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN_CHAT";;
    public static int getLanguages() {
        SharedPreferences sp = MyApp.Companion.getGetInstance().sInstance.getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE);
        return sp.getInt(LANGUAGES_TYPE,0);
    }

    public static String getAccessToken(){
        SharedPreferences sp = MyApp.Companion.getGetInstance().sInstance.getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE);
        return sp.getString(ACCESS_TOKEN,null);
    }

    public static void setAccessToken(String token){
        SharedPreferences sp = MyApp.Companion.getGetInstance().sInstance.getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE);
        sp.edit().putString(ACCESS_TOKEN, token).apply();
    }

}

package com.ramble.ramblewallet.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/17
 */
public class LanguageSetting {
    public static void setLanguage(Context context, int type) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (type == 1) { //0:简体中文、1：繁体中文、2：英文
                config.setLocale(Locale.SIMPLIFIED_CHINESE);
            } else if (type == 2) {
                config.setLocale(Locale.TRADITIONAL_CHINESE);
            } else if (type == 3) {
                config.setLocale(Locale.ENGLISH);
            }
        } else {
            if (type == 1) { //0:简体中文、1：繁体中文、2：英文
                config.locale = Locale.SIMPLIFIED_CHINESE;
            } else if (type == 2) {
                config.locale = Locale.TRADITIONAL_CHINESE;
            } else if (type == 3) {
                config.locale = Locale.ENGLISH;

            }
        }

        resources.updateConfiguration(config, dm);
    }
}

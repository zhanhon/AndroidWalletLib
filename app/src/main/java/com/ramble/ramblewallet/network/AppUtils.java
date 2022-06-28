package com.ramble.ramblewallet.network;

import static com.ramble.ramblewallet.constant.ConstantsKt.getAppContext;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;


/**
 * 时间　: 2021/12/21 14:20
 * 作者　: potato
 * 描述　:
 */
public class AppUtils {
    private AppUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isServiceRunning(String serviceClassName) {
        ActivityManager activityManager = (ActivityManager) getAppContext().getSystemService(
                Context.ACTIVITY_SERVICE
        );
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(
                Integer.MAX_VALUE
        );

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    public static String getTopActivityName() {
        ActivityManager activityManager = (ActivityManager) getAppContext().getSystemService(
                Context.ACTIVITY_SERVICE
        );
        if (activityManager != null) {
            return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        }
        return null;
    }

    public static String getPackageName() {
        return getAppContext().getPackageName();
    }

    public static String getVersionName() {
        PackageManager packageManager = getAppContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode() {
        PackageManager packageManager = getAppContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 服务端接口版本，每个版本立项时 找服务端获取并修改方法中的返回值，格式：YYMMDDXX，YYMMDD=年月日，XX=序列编号
     *
     * @return
     */
    public static String getApiVersion() {
        return "20220309";//格式：YYMMDDXX，YYMMDD=年月日，XX=序列编号
    }

    /**
     * secretKey 值
     *
     * @return
     */
    public static String getSecretKey() {
        return "6211249d5e6d4b829990853643c4b18c53d91b2aab0d469f8625a2ea66461c93";//格式：YYMMDDXX，YYMMDD=年月日，XX=序列编号
    }

    public static String getAppSignature() {
        PackageManager pm = getAppContext().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES
            );
            return packageInfo.signatures[0].toCharsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isAppDebug() {
        return isAppDebug(getPackageName());
    }

    public static boolean isAppDebug(String packageName) {
        try {
            PackageManager pm = getAppContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getAppName() {
        PackageManager pm = getAppContext().getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(getPackageName(), 0);
            return info.loadLabel(pm).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

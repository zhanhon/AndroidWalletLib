package com.ramble.ramblewallet.network;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.List;

import static com.ramble.ramblewallet.constant.ConstantsKt.APK_PACKAGE_ARCHIVE_TYPE;
import static com.ramble.ramblewallet.constant.ConstantsKt.getAppContext;
import static com.ramble.ramblewallet.constant.ConstantsKt.isDebug;
import static com.ramble.ramblewallet.network.AssertUtilsKt.unsupportedOperationException;


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
        return "21121701";//格式：YYMMDDXX，YYMMDD=年月日，XX=序列编号
    }

    /**
     * secretKey 值
     *
     * @return
     */
    public static String getSecretKey() {
        return "6211249d5e6d4b829990853643c4b18c53d91b2aab0d469f8625a2ea66461c93";//格式：YYMMDDXX，YYMMDD=年月日，XX=序列编号
    }

    public static void printExceptionForDebug(Exception e) {
        if (isDebug()) {
            e.printStackTrace();
        }
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

    public static boolean isAppInstalled(String packageName) {
        try {
            getAppContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean checkAndStartInstalledApp(Activity activity, String packageName) {
        if (isAppInstalled(packageName)) {
            Intent intent = getAppContext().getPackageManager().getLaunchIntentForPackage(packageName);
            activity.startActivity(intent);
            return true;
        } else {
            return false;
        }
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

    public static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static Intent getShareTextIntent(String content) {
        return new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, content)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getHomeIntent() {
        return new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME);
    }

    public static Intent getPickFileIntent() {
        return getPickIntent("file/*");
    }

    public static Intent getPickFileIntent(String fileExtension) {
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        if (type == null) {
            return null;
        }
        return getPickIntent(type);
    }

    public static Intent getPickPhotoIntent() {
        return getPickIntent("image/*");
    }

    public static Intent getPickVideoIntent() {
        return getPickIntent("video/*");
    }

    public static Intent getPickAudioIntent() {
        return getPickIntent("audio/*");
    }

    public static Intent getPickIntent(String type) {
        return new Intent(Intent.ACTION_GET_CONTENT)
                .setType(type)
                .addCategory(Intent.CATEGORY_OPENABLE);
    }

    public static Intent getTakePhotoIntent(File outputFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProviderUtils.getUriForFile(outputFile));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        }
        return intent;
    }

    public static Intent getCropPhotoIntent(@NonNull File inputFile,
                                            @NonNull File outputFile,
                                            int aspectX,
                                            int aspectY,
                                            int outputX,
                                            int outputY) {
        Uri inputUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            inputUri = FileProviderUtils.getUriForFile(inputFile);
        } else {
            inputUri = Uri.fromFile(inputFile);
        }
        return getCropPhotoIntent(inputUri, Uri.fromFile(outputFile), aspectX, aspectY, outputX, outputY);
    }

    public static Intent getCropPhotoIntent(@NonNull Uri inputUri,
                                            @NonNull Uri outputUri,
                                            int aspectX,
                                            int aspectY,
                                            int outputX,
                                            int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(inputUri, "image/*");
        intent.putExtra("crop", "true");
        // output width
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // output height
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        // output path and extension
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        if (outputUri.getPath().endsWith(".jpg") || outputUri.getPath().endsWith(".jpeg")) {
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        } else if (outputUri.getPath().endsWith(".png") || inputUri.getPath().endsWith(".png")) {
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        } else {
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        }
        intent.putExtra("return-data", false);
        return intent;
    }

    public static Intent getInstallAppIntent(@NonNull File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProviderUtils.getUriForFile(apkFile);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, APK_PACKAGE_ARCHIVE_TYPE);
        return intent;
    }

    public static Intent getUninstallAppIntent(@NonNull String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getLaunchAppIntent(@NonNull String packageName) {
        return getAppContext().getPackageManager().getLaunchIntentForPackage(packageName);
    }

    public static Intent getComponentIntent(@NonNull String packageName, @NonNull String className) {
        return new Intent(Intent.ACTION_VIEW)
                .setComponent(new ComponentName(packageName, className))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getDialIntent() {
        return new Intent(Intent.ACTION_DIAL)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getDialPhoneNumberIntent(@NonNull String phoneNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getCallPhoneNumberIntent(@NonNull String phoneNumber) {
        int granted = ActivityCompat.checkSelfPermission(
                getAppContext(),
                android.Manifest.permission.CALL_PHONE
        );
        if (granted == PackageManager.PERMISSION_GRANTED) {
            return new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            return unsupportedOperationException();
        }
    }

    public static Intent getSendSmsIntent(@NonNull String phoneNumber, @NonNull String content) {
        return new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber))
                .putExtra("sms_body", content)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getContactsIntent() {
        return new Intent(Intent.ACTION_VIEW)
                .setData(ContactsContract.Contacts.CONTENT_URI);
    }

    public static Intent getContactDetailIntent(long contactId, @NonNull String lookupKey) {
        Uri data = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
        return new Intent(Intent.ACTION_VIEW)
                .setDataAndType(data, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
    }

    public static Intent getSettingIntent() {
        return new Intent(Settings.ACTION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getAppDetailsSettingsIntent(@NonNull String packageName) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getAppsSettingIntent() {
        return new Intent(Settings.ACTION_APPLICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getWifiSettingIntent() {
        return new Intent(Settings.ACTION_WIFI_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getWirelessSettingIntent() {
        return new Intent(Settings.ACTION_WIRELESS_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getAccessibilitySettingIntent() {
        return new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void toInstallApp(@NonNull File apkFile) {
        getAppContext().startActivity(getInstallAppIntent(apkFile));
    }

    public static void toUninstallApp(@NonNull String packageName) {
        getAppContext().startActivity(getUninstallAppIntent(packageName));
    }

    public static void toLaunchApp(@NonNull String packageName) {
        getAppContext().startActivity(getLaunchAppIntent(packageName));
    }

    public static void toLaunchAppForResult(@NonNull Activity activity, @NonNull String packageName,
                                            int requestCode) {
        Intent intent = getAppContext().getPackageManager().getLaunchIntentForPackage(packageName);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toHome() {
        getAppContext().startActivity(getHomeIntent());
    }

    public static void toDialPhoneNumber(@NonNull String phoneNumber) {
        getAppContext().startActivity(getDialPhoneNumberIntent(phoneNumber));
    }

    public static void toCallPhoneNumber(@NonNull String phoneNumber) {
        getAppContext().startActivity(getCallPhoneNumberIntent(phoneNumber));
    }

    public static void toSendSms(@NonNull String phoneNumber, @NonNull String content) {
        getAppContext().startActivity(getSendSmsIntent(phoneNumber, content));
    }

    public static void toContacts() {
        getAppContext().startActivity(getContactsIntent());
    }

    public static void toContactDetail(long contactId, @NonNull String lookupKey) {
        getAppContext().startActivity(getContactDetailIntent(contactId, lookupKey));
    }

    public static void toPickFile(@NonNull Activity activity, int requestCode) {
        startActivityForResult(activity, null, getPickFileIntent(), requestCode);
    }

    public static void toPickFile(@NonNull Fragment fragment, int requestCode) {
        startActivityForResult(null, fragment, getPickFileIntent(), requestCode);
    }

    public static void toPickFile(@NonNull Activity activity, int requestCode,
                                  @NonNull String fileExtension) {
        startActivityForResult(activity, null, getPickFileIntent(fileExtension), requestCode);
    }

    public static void toPickPhoto(@NonNull Activity activity, int requestCode) {
        startActivityForResult(activity, null, getPickPhotoIntent(), requestCode);
    }

    public static void toPickPhoto(@NonNull Fragment fragment, int requestCode) {
        startActivityForResult(null, fragment, getPickPhotoIntent(), requestCode);
    }

    public static void toPickVideo(@NonNull Activity activity, int requestCode) {
        startActivityForResult(activity, null, getPickVideoIntent(), requestCode);
    }

    public static void toPickVideo(@NonNull Fragment fragment, int requestCode) {
        startActivityForResult(null, fragment, getPickVideoIntent(), requestCode);
    }

    public static void toPickAudio(@NonNull Activity activity, int requestCode) {
        startActivityForResult(activity, null, getPickAudioIntent(), requestCode);
    }

    public static void toPickAudio(@NonNull Fragment fragment, int requestCode) {
        startActivityForResult(null, fragment, getPickAudioIntent(), requestCode);
    }

    public static void toTakePhoto(@NonNull Activity activity, int requestCode,
                                   @NonNull File outputFile) {
        startActivityForResult(activity, null, getTakePhotoIntent(outputFile), requestCode);
    }

    public static void toTakePhoto(@NonNull Fragment fragment, int requestCode,
                                   @NonNull File outputFile) {
        startActivityForResult(null, fragment, getTakePhotoIntent(outputFile), requestCode);
    }

    public static void toCropPhoto(@NonNull Activity activity, int requestCode,
                                   @NonNull File inputFile, @NonNull File outputFile, int aspectX,
                                   int aspectY, int outputX, int outputY) {
        Intent intent = getCropPhotoIntent(inputFile, outputFile, aspectX, aspectY, outputX, outputY);
        startActivityForResult(activity, null, intent, requestCode);
    }

    public static void toCropPhoto(@NonNull Fragment fragment, int requestCode,
                                   @NonNull File inputFile, @NonNull File outputFile, int aspectX,
                                   int aspectY, int outputX, int outputY) {
        Intent intent = getCropPhotoIntent(inputFile, outputFile, aspectX, aspectY, outputX, outputY);
        startActivityForResult(null, fragment, intent, requestCode);
    }

    public static void toCropPhoto(@NonNull Activity activity, int requestCode,
                                   @NonNull Uri inputUri, @NonNull Uri outputUri, int aspectX,
                                   int aspectY, int outputX, int outputY) {
        Intent intent = getCropPhotoIntent(inputUri, outputUri, aspectX, aspectY, outputX, outputY);
        startActivityForResult(activity, null, intent, requestCode);
    }

    public static void toCropPhoto(@NonNull Fragment fragment, int requestCode,
                                   @NonNull Uri inputUri, @NonNull Uri outputUri, int aspectX,
                                   int aspectY, int outputX, int outputY) {
        Intent intent = getCropPhotoIntent(inputUri, outputUri, aspectX, aspectY, outputX, outputY);
        startActivityForResult(null, fragment, intent, requestCode);
    }

    public static void toSetting() {
        getAppContext().startActivity(getSettingIntent());
    }

    public static void toAppDetailSetting(String packageName) {
        getAppContext().startActivity(getAppDetailsSettingsIntent(packageName));
    }

    public static void toAppsSetting() {
        getAppContext().startActivity(getAppsSettingIntent());
    }

    public static void toWifiSetting() {
        getAppContext().startActivity(getWifiSettingIntent());
    }

    public static void toWirelessSetting() {
        getAppContext().startActivity(getWirelessSettingIntent());
    }

    public static void toAccessibilitySetting() {
        getAppContext().startActivity(getAccessibilitySettingIntent());
    }

    private static void startActivityForResult(@Nullable Activity activity,
                                               @Nullable Fragment fragment,
                                               @NonNull Intent intent,
                                               int requestCode) {
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            fragment.startActivityForResult(intent, requestCode);
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

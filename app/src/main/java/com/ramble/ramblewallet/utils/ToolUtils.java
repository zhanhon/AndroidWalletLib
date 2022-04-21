package com.ramble.ramblewallet.utils;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec.Builder;
import android.security.keystore.KeyProperties;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.ramble.ramblewallet.R;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 时间　: 2022/4/15 17:07
 * 作者　: potato
 * 描述　: 指纹验证弹窗
 */
public class ToolUtils {

    private static final String DEFAULT_KEY_NAME = "default_key";
    private static KeyStore keyStore;
    private static Cipher cipher;

    /**
     * 判断是否支持指纹识别
     */
    public static boolean supportFingerprint(Context mContext) {
        if (VERSION.SDK_INT < 23) {
//      MyToast.showToast("您的系统版本过低，不支持指纹功能");
            return false;
        } else {
            KeyguardManager keyguardManager = mContext.getSystemService(KeyguardManager.class);
            //指纹系统服务
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(mContext);
            //判断硬件是否支持指纹
            if (!fingerprintManager.isHardwareDetected()) {
                ToastUtils.showToastFree(mContext, mContext.getString(R.string.fingerprint_not_support));
                return false;
            } else if (!keyguardManager.isKeyguardSecure()) {
                //判断是否处于安全保护中（你的设备必须是使用屏幕锁保护的，这个屏幕锁可以是password，PIN或者图案都行）  判断 是否开启锁屏密码
                ToastUtils.showToastFree(mContext, mContext.getString(R.string.fingerprint_setting));
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                //设备支持指纹识别但是没有指纹数据
                ToastUtils.showToastFree(mContext, mContext.getString(R.string.fingerprint_need_add));
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    public static void initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            Builder builder = new Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(23)
    public static Cipher initCipher() {
        try {
            SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cipher;
    }

    public static void setVibrate(Context context) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(300);
    }


}

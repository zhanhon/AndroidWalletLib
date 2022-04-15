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

  private static KeyStore keyStore;
  private static Cipher cipher;
  private static final String DEFAULT_KEY_NAME = "default_key";

  /**
   *判断是否支持指纹识别
   */
  public static boolean supportFingerprint(Context mContext) {
    if (VERSION.SDK_INT < 23) {
//      MyToast.showToast("您的系统版本过低，不支持指纹功能");
      return false;
    } else {
      KeyguardManager keyguardManager = mContext.getSystemService(KeyguardManager.class);
      FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(mContext);
      if (!fingerprintManager.isHardwareDetected()) {
        ToastUtils.showToastFree(mContext,"您的手机不支持指纹功能");
        return false;
      } else if (!keyguardManager.isKeyguardSecure()) {
        ToastUtils.showToastFree(mContext,"您还未设置锁屏，请先设置锁屏并添加一个指纹");
        return false;
      } else if (!fingerprintManager.hasEnrolledFingerprints()) {
        ToastUtils.showToastFree(mContext,"您至少需要在系统设置中添加一个指纹");
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

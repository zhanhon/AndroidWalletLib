package com.ramble.ramblewallet.network


import android.Manifest
import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.constant.CN
import com.ramble.ramblewallet.constant.LANGUAGE
import com.ramble.ramblewallet.constant.REQUEST_CODE_1025
import com.ramble.ramblewallet.constant.appContext
import com.ramble.ramblewallet.utils.Md5Util
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import pub.devrel.easypermissions.EasyPermissions

/**
 * 时间　: 2021/12/21 14:14
 * 作者　: potato
 * 描述　:
 */
object ObjUtils {

    @JvmStatic
    fun <T : ApiRequest.Body> apiRequest(body: T, apiName: String): ApiRequest<T> {
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val currentTime = System.currentTimeMillis()
        var signOriginal: String = if (Gson().toJson(body).equals("{}")) {
            apiName + currentTime + "1" + AppUtils.getSecretKey()
        } else {
            apiName + currentTime + "1" + Gson().toJson(body) + AppUtils.getSecretKey()
        }
        val sign = Md5Util.md5(signOriginal)
        Log.v("-=-=->", "=========================Sign Original=========================")
        Log.v("-=-=->", signOriginal)
        Log.v("-=-=->", "=========================Request Start=========================")
        Log.v(
            "-=-=->", Gson().toJson(
                ApiRequest(
                    ApiRequest.Header(apiName, currentTime, 1, sign, languageCode),
                    body
                )
            )
        )
        return ApiRequest(
            ApiRequest.Header(apiName, currentTime, 1, sign, languageCode),
            body
        )
    }

    fun isCameraPermission(activity: Activity): Boolean {
        return if (EasyPermissions.hasPermissions(activity, Manifest.permission.CAMERA)) {
            true
        } else {
            EasyPermissions.requestPermissions(
                activity,
                activity.getString(R.string.alert_request_permission),
                REQUEST_CODE_1025,
                Manifest.permission.CAMERA
            )
            false
        }
    }
}
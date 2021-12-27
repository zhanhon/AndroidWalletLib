package com.ramble.ramblewallet.network


import com.ramble.ramblewallet.constant.CN
import com.ramble.ramblewallet.constant.LANGUAGE
import com.ramble.ramblewallet.constant.appContext
import com.ramble.ramblewallet.utils.Md5Util
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

/**
 * 时间　: 2021/12/21 14:14
 * 作者　: potato
 * 描述　:
 */
object ObjUtils {

    @JvmStatic
    fun <T : ApiRequest.Body> apiRequest(body: T, apiName: String): ApiRequest<T> {
        val getLanguage = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        var languageCode = "zh_CN"
        when (getLanguage) {
            "中" -> languageCode = "zh_CN"
            "繁" -> languageCode = "zh_TW"
            "En" -> languageCode = "EN"
        }
        val signStr = apiName + System.currentTimeMillis() + "1" + "" + AppUtils.getSecretKey()
        val sign = Md5Util.md5(signStr)
        return ApiRequest(
            ApiRequest.Header(apiName, System.currentTimeMillis(), 1, sign, languageCode),
            body
        )
    }
    
}
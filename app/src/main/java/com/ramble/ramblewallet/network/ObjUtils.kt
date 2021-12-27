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
        var language = "zh_CN"
        when (getLanguage) {
            "中" -> language = "zh_CN"
            "繁" -> language = "zh_TW"
            "En" -> language = "EN"
        }
        val signStr = apiName + System.currentTimeMillis() + "1" + "" + AppUtils.getSecretKey()
        val sign = Md5Util.md5(signStr)
        return ApiRequest(
            apiRequestHeader(apiName, sign, language),
            body
        )
    }

    @JvmStatic
    private fun apiRequestHeader(
        apiName: String,
        sign: String,
        languageCode: String
    ): ApiRequest.Header {
        return ApiRequest.Header(
            apiName,
            System.currentTimeMillis(),
            1,
            sign,
            languageCode
        )
    }
}
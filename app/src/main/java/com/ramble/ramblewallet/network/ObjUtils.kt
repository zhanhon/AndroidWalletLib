package com.ramble.ramblewallet.network


import com.google.gson.Gson
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
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val signStr = apiName + System.currentTimeMillis() + "1" + Gson().toJson(body) + AppUtils.getSecretKey()
        println("-=-=-=->signStr:${signStr}")
        val sign = Md5Util.md5(signStr)
        return ApiRequest(
            ApiRequest.Header(apiName, System.currentTimeMillis(), 1, sign, languageCode),
            body
        )
    }
    
}
package com.ramble.ramblewallet.network

import com.ramble.ramblewallet.utils.Zlib.Companion.getGzipEnabled
import com.squareup.moshi.Json

/**
 * 时间　: 2021/12/21 14:19
 * 作者　: potato
 * 描述　:
 */
class ApiRequest <T : ApiRequest.Body>(
    @Json(name = "header") val header: Header,
    @Json(name = "body") val body: T
) {

    open class Body @SuppressWarnings("unused") constructor()

    data class Header(//注意：com.square.domain.Api.kt 也有一个Header，每次修改 需保持两处同步
        @Json(name = "apiName") val apiName: String,
        @Json(name = "callTime") val callTime: Long,
        @Json(name = "clientType") val clientType: Int,
        @Json(name = "sign") val sign: String,
        @Json(name = "token") val token: String,
        @Json(name = "platformId") val platformId: Long,
        @Json(name = "clientVersion") val clientVersion: String = AppUtils.getVersionName(),
        @Json(name = "apiVersion") val apiVersion: String = AppUtils.getApiVersion(),
        @Json(name = "gzipEnabled") val gzipEnabled: Int = getGzipEnabled(),
        @Json(name = "deviceCode") val deviceCode: String = MMKVManager.getDeviceId()
    )

    override fun toString(): String {
        return "ApiRequest(header=$header, body=$body)"
    }
}
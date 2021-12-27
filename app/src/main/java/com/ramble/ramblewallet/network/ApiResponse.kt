package com.ramble.ramblewallet.network

import com.squareup.moshi.Json

/**
 * 时间　: 2021/12/23 13:31
 * 作者　: potato
 * 描述　:
 */
class ApiResponse <T>(
    @Json(name = "header") val header: Header?,
    @Json(name = "body") val body: Body<T>
) {

    fun code(): Int = body.code

    fun message(): String = body.message

    fun data(): T? = body.data

    class Body<T> {
        @Json(name = "code")
        var code = -1
            private set

        @Json(name = "message")
        var message = ""
            private set

        @Json(name = "data")
        var data: T? = null
            private set

    }

    //响应头
    class Header @SuppressWarnings("unused") constructor() {
        @Json(name = "merchantId")
        var merchantId = ""
            private set

        @Json(name = "apiName")  //API接口名
        var apiName = ""
            private set

        @Json(name = "callTime") //调用时间
        var callTime = 0L
            private set

        @Json(name = "token")
        var token = ""
            private set

        @Json(name = "sign") //参数签名
        var sign = ""
            private set

        @Json(name = "clientType") //客户端类型|1:Android|2:IOS|3:H5|4:PC|5:总后台|6:商户后台|7:运维后台|8:分分彩后台|9:无限代理后台|10:分享代理后台|11:现金代理后台|12:微聊后台|13:彩票主播(Android)|14:彩票主播(IOS)|15:彩票直播后台
        var clientType = -1
            private set

        @Json(name = "adminType")
        var adminType = ""
            private set

        @Json(name = "apiVersion") //(预留字段)Api版本号
        var apiVersion = ""
            private set

        @Json(name = "gzipEnabled") //(预留字段)是否启用gzip压缩
        var gzipEnabled = 0
            private set

        @Json(name = "languageCode") //平台ID
        var languageCode = ""
            private set
    }

}
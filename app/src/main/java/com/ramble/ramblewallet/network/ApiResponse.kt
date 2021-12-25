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
) : Envelope<T> {

    override fun status(): Boolean {
        return body.code == 200
    }

    override fun code(): Int = body.code

    override fun message(): String = body.message

    override fun data(): T? = body.data

    override fun toString(): String {
        return "ApiResponse(header=$header, body=$body)"
    }

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

        override fun toString(): String {
            return "Body(code=$code, message='$message', data=$data)"
        }
    }

    //响应头
    class Header @SuppressWarnings("unused") constructor() {
        @Json(name = "apiName")
        var apiName = ""
            //API接口名
            private set

        @Json(name = "callTime")
        var callTime = 0L
            //调用时间
            private set

        @Json(name = "languageCode")
        var languageCode = "1"
            //平台ID
            private set

        @Json(name = "clientType")
        var clientType = -1
            //客户端类型|1:Android|2:IOS|3:H5|4:PC|5:总后台|6:商户后台|7:运维后台|8:分分彩后台|9:无限代理后台|10:分享代理后台|11:现金代理后台|12:微聊后台|13:彩票主播(Android)|14:彩票主播(IOS)|15:彩票直播后台
            private set

        @Json(name = "sign")
        var sign = ""
            //参数签名
            private set

        @Json(name = "apiVersion")
        var apiVersion = "1"
            //(预留字段)Api版本号
            private set

        @Json(name = "gzipEnabled")
        var gzipEnabled = 0
            //(预留字段)是否启用gzip压缩
            private set



        override fun toString(): String {
            return "ApiHeader(apiName='$apiName', callTime=$callTime, clientType=$clientType, sign='$sign')"
        }
    }

    @Deprecated("Removed")
    abstract class Data
}
package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * 时间　: 2022/1/30 11:34
 * 作者　: potato
 * 描述　:
 */
class PrivacyPolicyInfo  @SuppressWarnings("unused") constructor() {







    class Req(
        @Json(name = "status") var status: Int = -1,//状态 1:开启|0:关闭
        @Json(name = "lang") var lang: Int = -1,//公告语言1:简体中文|2:繁体中文|3:英文
        @Json(name = "type") var type: String = ""//类型 privacy:隐私声明|service:服务协议
    ) : ApiRequest.Body()
}
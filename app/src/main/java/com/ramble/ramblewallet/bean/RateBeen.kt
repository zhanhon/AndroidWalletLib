package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * 时间　: 2021/12/25 15:24
 * 作者　: potato
 * 描述　:
 */
class RateBeen @SuppressWarnings("unused") constructor() {

    @Json(name = "change")
    var change: String? = ""//汇率改变百分比

    @Json(name = "currencyType")
    var currencyType: String? = ""//币种

    @Json(name = "rateCny")//人民币汇率
    var rateCny: String = ""

    @Json(name = "rateHkd")
    var rateHkd: String = ""//港元汇率

    @Json(name = "rateUsd")//美元汇率
    var rateUsd: String = ""


    class Req @SuppressWarnings("unused") constructor() : ApiRequest.Body()
}
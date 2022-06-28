package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/26
 */
class BtcMinerConfig @SuppressWarnings("unused") constructor() {

    @Json(name = "fastestFee")
    var fastestFee: String? = ""//快速Fee

    @Json(name = "generalFee")
    var generalFee: String? = ""//一般Fee

    class Req @SuppressWarnings("unused") constructor() : ApiRequest.Body()
}
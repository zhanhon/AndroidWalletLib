package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/26
 */
class DogeMinerConfig @SuppressWarnings("unused") constructor() {

    @Json(name = "generalFee")
    var generalFee: String? = ""//推荐一般速度费率 每个字节的doge

    class Req @SuppressWarnings("unused") constructor() : ApiRequest.Body()
}
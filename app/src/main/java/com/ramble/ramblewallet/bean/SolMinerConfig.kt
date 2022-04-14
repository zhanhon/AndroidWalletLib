package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/26
 */
class SolMinerConfig @SuppressWarnings("unused") constructor() {

    @Json(name = "recommendFee")
    var recommendFee: String? = ""//推荐使用费（10s更新一次）

    class Req @SuppressWarnings("unused") constructor() : ApiRequest.Body()
}
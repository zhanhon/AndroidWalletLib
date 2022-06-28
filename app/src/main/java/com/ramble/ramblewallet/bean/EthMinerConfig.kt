package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/26
 */
class EthMinerConfig @SuppressWarnings("unused") constructor() {

    @Json(name = "currencyType")
    var currencyType: String? = ""//币种|ETH|USDT|...

    @Json(name = "fastGasPrice")
    var fastGasPrice: String? = ""//快速gasPrice

    @Json(name = "gasLimit")
    var gasLimit: String? = ""//gas限制

    @Json(name = "slowGasPrice")
    var slowGasPrice: String? = ""//慢速gasPrice

    class Req(
        @Json(name = "currencyType") //币种|ETH|USDT|...
        val currencyType: String
    ) : ApiRequest.Body()

}
package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/26
 */
class AddressReport @SuppressWarnings("unused") constructor() {

    class Req(
        @Json(name = "detailsList") //地址列表
        val detailsList: List<DetailsList>,
        @Json(name = "deviceToken") //友盟deviceToken
        val deviceToken: String,
        @Json(name = "language") //钱包语言|zh_CN:简体中文|zh_TW:繁体中文|en:英文|th:泰语
        val language: String
    ) : ApiRequest.Body()

    class DetailsList(
        var address: String = "", //地址
        var addressType: Int = 0 //链类型|0:ETC|1:ETH|2:TRON
    )
}
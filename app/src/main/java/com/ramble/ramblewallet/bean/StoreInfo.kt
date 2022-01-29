package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json
import java.io.Serializable

/**
 * 时间　: 2022/1/11 16:19
 * 作者　: potato
 * 描述　:
 */
class StoreInfo @SuppressWarnings("unused") constructor() : Serializable {


    @Json(name = "contractAddress")
    var contractAddress: String = ""//contractAddress

    @Json(name = "createTime")
    var createTime: String = ""//创建时间

    @Json(name = "id")
    var id: Int = 0//主键ID

    @Json(name = "name")
    var name: String = ""//代币

    @Json(name = "platform")
    var platform: Platform? = null//主链币


    @Json(name = "platformId")
    var platformId: Int = -1//platformId

    @Json(name = "quote")
    var quote: List<Record> = listOf()

    @Json(name = "rank")
    var rank: Int = 0//rank

    @Json(name = "slug")
    var slug: String = ""//

    @Json(name = "sourceType")
    var sourceType: String = ""//

    @Json(name = "symbol")
    var symbol: String = ""//

    @Json(name = "updateTime")
    var updateTime: String = ""//修改时间

    var isMyToken: Int = 0//是否我的资产0,不是 1 是的

    class Record : Serializable {
        @Json(name = "cryptoId")
        var cryptoId: Int = -1//交易数量

        @Json(name = "symbol")
        var symbol: String = ""//币种

        @Json(name = "price")
        var price: Double = 0.00//转换金额

        @Json(name = "lastUpdated")
        var lastUpdated: Long = -1//转换单位
    }

    class Platform : Serializable {
        @Json(name = "id")
        var id: Int = 0//主键ID

        @Json(name = "name")
        var name: String = ""//代币

        @Json(name = "symbol")
        var symbol: String = ""//

        @Json(name = "slug")
        var slug: String = ""//

        @Json(name = "token_address")
        var token_address: String = ""//
    }

    class Req(
        @Json(name = "condition") var condition: String? = null,//查询条件 name|symbol
        @Json(name = "convertId") var convertId: String? = null,//代币转换 2781,2787,2792
        @Json(name = "name") var name: String? = null,//代币
        @Json(name = "platformId") var platformId: Int? = null,//主链币
        @Json(name = "symbol") var symbol: String? = null//代币标识
    ) : ApiRequest.Body()

}



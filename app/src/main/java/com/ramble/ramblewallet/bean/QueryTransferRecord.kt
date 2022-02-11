package com.ramble.ramblewallet.bean


import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json
import java.io.Serializable

/**
 * 时间　: 2021/12/20 11:53
 * 作者　: potato
 * 描述　:
 */
class QueryTransferRecord @SuppressWarnings("unused") constructor() {

    @Json(name = "pageNo")
    var pageNo: Int = 0//当前页码

    @Json(name = "pageSize")
    var pageSize: Int = 0//每页显示条数

    @Json(name = "records")
    var records: List<Record> = listOf()

    @Json(name = "totalCount")
    var totalCount: Int = 0//总记录数

    @Json(name = "totalPage")
    var totalPage: Int = 0//总页数


    class Record : Serializable {
        @Json(name = "addressType")
        var addressType: Int = 0//主链类型（链）|1:ETH以太坊地址|2:TRX波场地址|3:比特币地址

        @Json(name = "amount")
        var amount: String = ""//交易数量

        @Json(name = "blockNumber")
        var blockNumber: String = ""//区块编号

        @Json(name = "changeAmount")
        var changeAmount: Double = 0.00//转换金额

        @Json(name = "changeUnit")
        var changeUnit: String = ""//转换单位

        @Json(name = "createTime")
        var createTime: String = ""//创建时间

        @Json(name = "currencyType")
        var currencyType: String = ""//币种名称

        @Json(name = "fromAddress")
        var fromAddress: String = ""//转出地址

        @Json(name = "gasLimit")
        var gasLimit: String = ""//gas_limit

        @Json(name = "gasPrice")
        var gasPrice: String = ""//gas_price

        @Json(name = "id")
        var id: Int = 0//id

        @Json(name = "inputs")
        var inputs: List<InRecord> = listOf()//BTC转出信息

        @Json(name = "miner")
        var miner: String = ""//矿工费

        @Json(name = "minerUnit")
        var minerUnit: String = ""//矿工费单位

        @Json(name = "outputs")
        var outputs: List<OutRecord> = listOf()//BTC转入信息

        @Json(name = "remark")
        var remark: String = ""//备注

        @Json(name = "status")
        var status: Int = 0//区块交易状态|1:广播中|2:成功|3:失败

        @Json(name = "statusDesc")
        var statusDesc: String = ""//区块交易状态desc|1:广播中|2:成功|3:失败

        @Json(name = "toAddress")
        var toAddress: String = ""//转入地址

        @Json(name = "transferType")
        var transferType: Int = 0//交易类型|1:转出|2:转入

        @Json(name = "txHash")
        var txHash: String = ""//交易hash

        @Json(name = "unit")
        var unit: String = ""//交易币种单位
    }

    class InRecord : Serializable {
        @Json(name = "address")
        var address: String = ""//地址

        @Json(name = "amount")
        var amount: Double = 0.00//金额

        @Json(name = "changeAmount")
        var changeAmount: Double = 0.00//转换金额

        @Json(name = "changeUnit")
        var changeUnit: String = ""//转换单位

        @Json(name = "currencyType")
        var currencyType: String = ""//币种

        @Json(name = "unit")
        var unit: String = ""//交易币种单位
    }

    class OutRecord : Serializable {
        @Json(name = "address")
        var address: String = ""//地址

        @Json(name = "amount")
        var amount: Double = 0.00//金额

        @Json(name = "changeAmount")
        var changeAmount: Double = 0.00//转换金额

        @Json(name = "changeUnit")
        var changeUnit: String = ""//转换单位

        @Json(name = "currencyType")
        var currencyType: String = ""//币种

        @Json(name = "unit")
        var unit: String = ""//交易币种单位
    }

    data class Req(
        @Json(name = "pageNo")
        var pageNo: Int,   //当前页码
        @Json(name = "pageSize")
        var pageSize: Int = 20,   //每页显示条数
        @Json(name = "address")
        var address: String,  //地址
        @Json(name = "addressType")
        var addressType: Int,   //地址类型（链）|1:ETH以太坊地址|2:TRX波场地址|3:比特币地址
        @Json(name = "changeCurrencyType")
        var changeCurrencyType: Int,   //转换币种类型|默认为1:USD|2:CNY|3:HKD
        @Json(name = "endTimeMillis")
        var endTimeMillis: Long?,  //查询结束时间
        @Json(name = "startTimeMillis")
        var startTimeMillis: Long?, //查询开始时间
        @Json(name = "status")
        var status: Int? = null,   //交易记录状态|3:失败|其它null
        @Json(name = "transferType")
        var transferType: Int? = null  //交易类型|1:转出|2:转入|其它null
    ) : ApiRequest.Body()

    override fun toString(): String {
        return "QueryWashPage(pageNo=$pageNo, pageSize=$pageSize, records=$records, totalCount=$totalCount, totalPage=$totalPage)"
    }
}

package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json


/**
 * 时间　: 2022/3/15 13:54
 * 作者　: potato
 * 描述　:
 */
class ReportTransferInfo @SuppressWarnings("unused") constructor() {
    data class Req(
        @Json(name = "addressType")
        var addressType: Int,   //地址类型（链）|1:ETH以太坊地址|2:TRX波场地址|3:比特币地址
        @Json(name = "amount")
        var amount: String?,  //交易数量,实际输入的转账金额，没有换算的
        @Json(name = "contractAddress")
        var contractAddress: String?,   //代币交易合约地址(主币转账时为空)
        @Json(name = "currencyType")
        var currencyType: String?,  //币种名称
        @Json(name = "fromAddress")
        var fromAddress: String?,   //转出地址
        @Json(name = "gasLimit")
        var gasLimit: String?,   //gas_limit
        @Json(name = "gasPrice")
        var gasPrice: String?,  //gas_price 单位WEI
        @Json(name = "inputs")
        var inputs: ArrayList<InRecord>? = arrayListOf(),//BTC转出信息
        @Json(name = "outputs")
        var outputs:  ArrayList<InRecord>? = arrayListOf(),//BTC转入信息
        @Json(name = "toAddress")
        var toAddress: String?, //转入地址
        @Json(name = "txHash")
        var txHash:  String?,   //交易hash
    ) : ApiRequest.Body()

    data  class InRecord(
        @Json(name = "address")
        var address: String ?= "",//地址

        @Json(name = "amount")
        var amount: String ?= "",//金额

        @Json(name = "num")
        var num: Int = 0//交易顺序no
    )

}
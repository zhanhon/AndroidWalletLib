package com.ramble.ramblewallet.network

import com.ramble.ramblewallet.bean.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 时间　: 2021/12/25 15:19
 * 作者　: potato
 * 描述　:
 */


const val reportAddressUrl = "wallet-decentralized-api/address/report"
const val getEthMinerConfigUrl = "wallet-decentralized-api/sys/getEthMinerConfig"
const val rateInfoUrl = "wallet-decentralized-api/sys/getRateInfo"


interface ApiService {

    /** 查询站内信 */
    @POST("lottery-api/userLetter/queryPageUserLetter")
    fun getUserLetterPage(@Body req: ApiRequest<Page.Req>): Observable<ApiResponse<Page>>


    /** APP端地址上传 */
    @POST(reportAddressUrl)
    fun putAddress(@Body req: ApiRequest<AddressReport.Req>): Observable<ApiResponse<Any>>

    /** 查询以太坊矿工费 */
    @POST(getEthMinerConfigUrl)
    fun getEthMinerConfig(@Body req: ApiRequest<EthMinerConfig.Req>): Observable<ApiResponse<EthMinerConfig>>

    /** 查询汇率相关信息 */
    @POST(rateInfoUrl)
    fun getRateInfo(@Body req: ApiRequest<EmptyReq>): Observable<ApiResponse<List<RateBeen>>>


}
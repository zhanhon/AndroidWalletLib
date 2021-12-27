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
interface ApiService {

    /** 查询站内信 */
    @POST("lottery-api/userLetter/queryPageUserLetter")
    fun getUserLetterPage(@Body req: ApiRequest<Page.Req>): Observable<ApiResponse<Page>>

    /** 查询站内信 */
    @POST("wallet-decentralized-api/sys")
    fun getInMessage(@Body req: ApiRequest<RateBeen.Req>): Observable<ApiResponse<RateBeen>>


    /** APP端地址上传 */
    @POST("wallet-decentralized-api/address/report")
    fun putAddress(@Body req: ApiRequest<AddressReport.Req>): Observable<ApiResponse<Any>>

    /** 查询以太坊矿工费 */
    @POST("wallet-decentralized-api/sys/getEthMinerConfig")
    fun getEthMinerConfig(@Body req: ApiRequest<EthMinerConfig.Req>): Observable<ApiResponse<EthMinerConfig>>

    /** 查询汇率相关信息 */
    @POST("wallet-decentralized-api/sys/getRateInfo")
    fun getRateInfo(@Body req: ApiRequest<EmptyReq>): Observable<ApiResponse<RateBeen>>


}
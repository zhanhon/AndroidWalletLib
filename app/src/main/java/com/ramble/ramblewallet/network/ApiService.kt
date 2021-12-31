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
const val transferInfoUrl = "wallet-decentralized-api/transfer/info"
const val noticeInfoUrl = "wallet-decentralized-api/sys/getNotice"


interface ApiService {

    /** 查询公告列表 */
    @POST(noticeInfoUrl)
    fun getNotice(@Body req: ApiRequest<Page.Req>): Observable<ApiResponse<Page>>

    /** 根据地址获取交易记录 */
    @POST(transferInfoUrl)
    fun getTransferInfo(@Body req: ApiRequest<QueryTransferRecord.Req>): Observable<ApiResponse<QueryTransferRecord>>

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
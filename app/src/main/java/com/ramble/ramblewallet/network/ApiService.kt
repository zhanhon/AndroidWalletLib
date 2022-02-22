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
const val getBtcMinerConfigUrl = "wallet-decentralized-api/sys/getBtcMinerConfig"
const val transferInfoUrl = "wallet-decentralized-api/transfer/info"
const val noticeInfoUrl = "wallet-decentralized-api/sys/getNotice"
const val faqInfoUrl = "wallet-decentralized-api/faq/getFaqInfos"
const val queryFaqInfoUrl = "wallet-decentralized-api/faq/queryAllFaqByType"
const val getStoreUrl = "wallet-decentralized-api/sys/listStoreCoinmarketcap"
const val getPrivacyInfo = "wallet-decentralized-api/sys/getPrivacyPolicy"

interface ApiService {

    /** 查询公告列表 */
    @POST(noticeInfoUrl)
    fun getNotice(@Body req: ApiRequest<Page.Req>): Observable<ApiResponse<Page>>

    /** 根据地址获取交易记录 */
    @POST(transferInfoUrl)
    fun getTransferInfo(@Body req: ApiRequest<QueryTransferRecord.Req>): Observable<ApiResponse<QueryTransferRecord>>

    /** 查询帮助中心列表 */
    @POST(faqInfoUrl)
    fun getFaqInfos(@Body req: ApiRequest<FaqInfos.Req>): Observable<ApiResponse<FaqInfos>>

    /** 获取分类下常见问题列表 */
    @POST(queryFaqInfoUrl)
    fun queryAllFaqByType(@Body req: ApiRequest<QueryFaqInfos.Req>): Observable<ApiResponse<List<QueryFaqInfos>>>

    /** 代币查询 */
    @POST(getStoreUrl)
    fun getStore(@Body req: ApiRequest<StoreInfo.Req>): Observable<ApiResponse<List<StoreInfo>>>

    /** 查询隐私条款 */
    @POST(getPrivacyInfo)
    fun getPrivacyPolicy(@Body req: ApiRequest<PrivacyPolicyInfo.Req>): Observable<ApiResponse<PrivacyPolicyInfo>>

    /** APP端地址上传 */
    @POST(reportAddressUrl)
    fun putAddress(@Body req: ApiRequest<AddressReport.Req>): Observable<ApiResponse<Any>>

    /** 查询以太坊矿工费 */
    @POST(getEthMinerConfigUrl)
    fun getEthMinerConfig(@Body req: ApiRequest<EthMinerConfig.Req>): Observable<ApiResponse<EthMinerConfig>>

    /** 查询比特币矿工费 */
    @POST(getBtcMinerConfigUrl)
    fun getBtcMinerConfig(@Body req: ApiRequest<BtcMinerConfig.Req>): Observable<ApiResponse<BtcMinerConfig>>


}
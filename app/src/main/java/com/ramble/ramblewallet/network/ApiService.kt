package com.ramble.ramblewallet.network

import com.ramble.ramblewallet.bean.*
import com.ramble.ramblewallet.update.AppVersion
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
const val getSolMinerConfigUrl = "wallet-decentralized-api/sys/getSolMinerConfig"
const val getDogeMinerConfigUrl = "wallet-decentralized-api/sys/getDogeMinerConfig"
const val transferInfoUrl = "wallet-decentralized-api/transfer/info"
const val noticeInfoUrl = "wallet-decentralized-api/sys/getNotice"
const val faqInfoUrl = "wallet-decentralized-api/faq/getFaqInfos"
const val queryFaqInfoUrl = "wallet-decentralized-api/faq/queryAllFaqByType"
const val getStoreUrl = "wallet-decentralized-api/sys/listStoreCoinmarketcap"
const val getPrivacyInfo = "wallet-decentralized-api/sys/getPrivacyPolicy"
const val reportTransferUrl = "wallet-decentralized-api/transfer/reportTransferRecord"
const val getAppVersion = "/wallet-decentralized-api/app-version/queryNewAppVersion"


interface ApiService {

    /** 查询公告列表 */
    @POST(noticeInfoUrl)
    fun getNotice(@Body req: ApiRequest<Page.Req>): Observable<ApiResponse<Page>>

    /** 根据地址获取交易记录 */
    @POST(transferInfoUrl)
    fun getTransferInfo(@Body req: ApiRequest<QueryTransferRecord.Req>): Observable<ApiResponse<QueryTransferRecord>>

    /** 上报转出交易记录 */
    @POST(reportTransferUrl)
    fun reportTransferRecord(@Body req: ApiRequest<ReportTransferInfo.Req>): Observable<ApiResponse<Any>>

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

    /** 查询索拉纳币矿工费 */
    @POST(getSolMinerConfigUrl)
    fun getSolMinerConfig(@Body req: ApiRequest<SolMinerConfig.Req>): Observable<ApiResponse<SolMinerConfig>>

    /** 查询狗狗币矿工费 */
    @POST(getDogeMinerConfigUrl)
    fun getDogeMinerConfig(@Body req: ApiRequest<DogeMinerConfig.Req>): Observable<ApiResponse<DogeMinerConfig>>

    /**  更新版本 */
    @POST(getAppVersion)
    fun appVersion(@Body req: ApiRequest<AppVersion.Req>): Observable<ApiResponse<AppVersion>>
}
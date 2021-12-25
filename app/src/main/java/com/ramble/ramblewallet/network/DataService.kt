package com.ramble.ramblewallet.network

import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.bean.RateBeen
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 时间　: 2021/12/25 15:19
 * 作者　: potato
 * 描述　:
 */
interface DataService {

    /** 查询站内信 */
    @POST("lottery-api/userLetter/queryPageUserLetter")
    fun getUserLetterPage(@Body req: ApiRequest<Page.Req>): Observable<ApiResponse<Page>>

    /** 查询站内信 */
    @POST("wallet-decentralized-api/sys")
    fun getRateInfo(@Body req: ApiRequest<RateBeen.Req>): Observable<ApiResponse<RateBeen>>
}
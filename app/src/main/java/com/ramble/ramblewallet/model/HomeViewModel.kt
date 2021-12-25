package com.ramble.ramblewallet.model

import androidx.lifecycle.MutableLiveData
import com.ramble.ramblewallet.base.RxViewModel
import com.ramble.ramblewallet.bean.RateBeen
import com.ramble.ramblewallet.network.ApiResponse
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.applyIo
import io.reactivex.Observable
import javax.inject.Inject

/**
 * 时间　: 2021/12/25 15:29
 * 作者　: potato
 * 描述　:
 */
class HomeViewModel@Inject constructor() : RxViewModel()  {
    val card = MutableLiveData<RateBeen>()

    /** 基息查询*/
    fun getUserTransferInfo(): Observable<ApiResponse<RateBeen>> {
        return dataService.getRateInfo(RateBeen.Req().toApiRequest()).applyIo()
    }
}
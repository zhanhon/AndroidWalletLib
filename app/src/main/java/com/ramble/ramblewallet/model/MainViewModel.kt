package com.ramble.ramblewallet.model

import com.ramble.ramblewallet.base.RxViewModel
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.EmptyReq
import com.ramble.ramblewallet.bean.EthMinerConfig
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
class MainViewModel @Inject constructor() : RxViewModel() {

    /** 基息查询*/
    fun getUserTransferInfo(): Observable<ApiResponse<RateBeen>> {
        return dataService.getInMessage(RateBeen.Req().toApiRequest()).applyIo()
    }


    /** APP端地址上传 */
    fun putAddress(req: AddressReport.Req): Observable<ApiResponse<Any>> {
        return dataService.putAddress(req.toApiRequest()).applyIo()
    }

    /** 查询以太坊矿工费 */
    fun getEthMinerConfig(req: EthMinerConfig.Req): Observable<ApiResponse<EthMinerConfig>> {
        return dataService.getEthMinerConfig(req.toApiRequest()).applyIo()
    }

    /** 查询汇率相关信息 */
    fun getRateInfo(): Observable<ApiResponse<RateBeen>> {
        return dataService.getRateInfo(EmptyReq().toApiRequest()).applyIo()
    }

}
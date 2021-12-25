package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * 时间　: 2021/12/16 9:48
 * 作者　: potato
 * 描述　:
 */
class Page @SuppressWarnings("unused") constructor()  {

    var pageNo: Int = 0


    var pageSize: Int = 0


    var records: List<Record> = listOf()


    var totalCount: Int = 0


    var totalPage: Int = 0

    class Record(
        var content: String = "",
        var createTime: String = "",
        var id: Int = 0,
         var isReaded: Int = 0,
        var isPopup: Int = 0,
       var isPush: Int = 0,
         var platformId: Int = 0,
        var title: String = ""
    )

    class Req(
        @Json(name = "pageNo") val pageNo: Int,//当前页码
        @Json(name = "pageSize") val pageSize: Int, //每页显示条数
        @Json(name = "isReaded") var isReaded: Int = -1
    ) : ApiRequest.Body() {
        override fun toString(): String {
            return "Req(pageNo=$pageNo, pageSize=$pageSize, isReaded=$isReaded)"
        }
    }

}
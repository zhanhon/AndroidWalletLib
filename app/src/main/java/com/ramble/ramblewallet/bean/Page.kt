package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * 时间　: 2021/12/16 9:48
 * 作者　: potato
 * 描述　:
 */
class Page @SuppressWarnings("unused") constructor() {
    @Json(name = "pageNo")
    var pageNo: Int = 0

    @Json(name = "pageSize")
    var pageSize: Int = 0

    @Json(name = "records")
    var records: List<Record> = listOf()

    @Json(name = "totalCount")
    var totalCount: Int = 0

    @Json(name = "totalPage")
    var totalPage: Int = 0

    class Record(
        @Json(name = "content")
        var content: String = "",//公告内容
        @Json(name = "title")
        var title: String = "",//标题
        @Json(name = "id")
        var id: Int = 0,//公告ID
        var isRead: Int = 0,//是否已读
        @Json(name = "lang")
        var lang: Int = 0//公告语言1:简体中文|2:繁体中文|3:英文

    )

    class Req(
        @Json(name = "pageNo") val pageNo: Int,//当前页码
        @Json(name = "pageSize") val pageSize: Int, //每页显示条数
        @Json(name = "lang") var lang: Int = -1//公告语言1:简体中文|2:繁体中文|3:英文
    ) : ApiRequest.Body() {
        override fun toString(): String {
            return "Req(pageNo=$pageNo, pageSize=$pageSize, lang=$lang)"
        }
    }

}
package com.ramble.ramblewallet.bean

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

/**
 * 时间　: 2022/1/6 16:52
 * 作者　: potato
 * 描述　:
 */
class FaqInfos @SuppressWarnings("unused") constructor() {

    @Json(name = "categoryList")
    var categoryList: List<CategoryReq> = listOf()


    @Json(name = "noviceList")
    var noviceList: List<NoviceReq> = listOf()

    class NoviceReq {
        @Json(name = "category")
        var category: Int = 0//分类1:钱包相关|2:认识区块链|3:技术问题

        @Json(name = "categoryName")
        var categoryName: String = ""//分类名称

        @Json(name = "clientType")
        var clientType: Int = 0//终端类型1:Android|2:ios

        @Json(name = "content")
        var content: String = ""//内容

        @Json(name = "createTime")
        var createTime: String = ""//创建时间

        @Json(name = "id")
        var id: Int = 0//公告ID

        @Json(name = "lang")
        var lang: Int = -1//公告语言1:简体中文|2:繁体中文|3:英文

        @Json(name = "noviceDisplay")
        var noviceDisplay: Int = 0//是否显示在新手上路中1:显示|0:不显示

        @Json(name = "resolvedTimes")
        var resolvedTimes: Int = 0//已解决次数

        @Json(name = "sort")
        var sort: Int = 0//排序（越小越靠前）

        @Json(name = "status")
        var status: Int = 0//0:关闭|1:开启

        @Json(name = "title")
        var title: String = ""//标题

        @Json(name = "unresolvedTimes")
        var unresolvedTimes: Int = 0//未解决次数
    }

    class CategoryReq {
        @Json(name = "id")
        var id: Int = 0//公告ID

        @Json(name = "name")
        var name: String = ""//分类名称

        @Json(name = "sort")
        var sort: Int = 0//排序（越小越靠前）
    }

    class Req(
        @Json(name = "lang") var lang: Int = -1//公告语言1:简体中文|2:繁体中文|3:英文
    ) : ApiRequest.Body()

}



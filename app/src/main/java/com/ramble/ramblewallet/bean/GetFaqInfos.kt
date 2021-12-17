package com.ramble.ramblewallet.bean

/**
 * 时间　: 2021/12/17 9:33
 * 作者　: potato
 * 描述　:
 */
class GetFaqInfos  @SuppressWarnings("unused") constructor() {

    var faqTypeLists: List<FaqTypeList> = listOf()


    var hotFaqLists: List<HotFaqList> = listOf()

    class FaqTypeList(
     var createTime: String? = "",
        var id: Int? = 0,
         var platformId: Int? = 0,
         var sortOrder: Int? = 0,
        var typeName: String? = ""
    )

    class HotFaqList(
        var content: String? = "", //问题内容
        var createTime: String? = "", //创建时间
       var faqTypeId: Int? = 0, //问题类型ID(faq_type表的id)
       var id: Int? = 0, //ID
        var isHot: Int? = 0,  //是否热门|0:非热门|1:热门
        var platformId: Int? = 0, //平台ID
        var resolvedTimes: Int? = 0,  //已解决次数
       var sortOrder: Int? = 0,  //排序
        var title: String? = "",  //问题标题
        var unresolvedTimes: Int? = 0,  //未解决次数
       var faqTypeName: String? = ""  //问题类型名称
    )

    class Req @SuppressWarnings("unused") constructor()

}
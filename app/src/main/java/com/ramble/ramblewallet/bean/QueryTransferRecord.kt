package com.ramble.ramblewallet.bean

import android.os.Parcelable

/**
 * 时间　: 2021/12/20 11:53
 * 作者　: potato
 * 描述　:
 */
class QueryTransferRecord @SuppressWarnings("unused") constructor() {


        var pageNo: Int = 0


        var pageSize: Int = 0


        var records: List<Record> = listOf()


        var totalCount: Int = 0


        var totalPage: Int = 0


        class Record(

            var actionStatus: Int = 0,//操作状态1 待操作 2通过 3拒绝 (提币交易类型)

            var blockStatus: Int = 0,//区块交易状态1 广播中 2成功 3失败

            var systemSentWashBetAmount: Double = 0.00,//系统发放洗码金金额(发放洗码金)

            var createTime: String = "",////创建时间

            var thirdGameName: String = "",//三方游戏名称

            var userReceivedWashBetAmount: Double = 0.00,//用户领取洗码金金额(已发放)

            var validBetAmount: Double = 0.0//有效投注
        )

//        class Req(
//            @Json(name = "pageNo")
//            val pageNo: Int,   //当前页码
//            @Json(name = "pageSize")
//            val pageSize: Int,   //每页显示条数
//            @Json(name = "queryTimeEnd")
//            val queryTimeEnd: String,  //查询结束时间
//            @Json(name = "queryTimeStart")
//            val queryTimeStart: String  //查询开始时间
//        ) : ApiRequest.Body()

        override fun toString(): String {
            return "QueryWashPage(pageNo=$pageNo, pageSize=$pageSize, records=$records, totalCount=$totalCount, totalPage=$totalPage)"
        }
    }

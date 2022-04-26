package com.ramble.ramblewallet.update

import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

class AppVersion @SuppressWarnings("unused") constructor() {

    @Json(name = "artificialShow")
    var artificialShow: Int? = -1//是否手动版本提示更新内容：0-否，1-是

    @Json(name = "content")
    var content: String? = ""//app提示内容

    @Json(name = "date")
    var date: String? = ""//版本更新时间

    @Json(name = "forcedUpdatingShow")
    var forcedUpdatingShow: Int? = -1//是否强制更新：0-否，1-是

    @Json(name = "floatingWindowShow")
    var floatingWindowShow: Int? = -1//是否弹窗提示更新内容：0-否，1-是

    @Json(name = "url")
    var updateUrl: String? = ""//APP更新地址

    @Json(name = "version")
    var version: String? = ""//APP版本号

    class Req : ApiRequest.Body() {
        @Json(name = "appType")
        var appType: Int = 1
            //APP类型|1:android|2:ios
            private set

    }
}

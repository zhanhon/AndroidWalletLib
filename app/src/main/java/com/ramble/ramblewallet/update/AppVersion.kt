package com.ramble.ramblewallet.update

import com.ramble.ramblewallet.BuildConfig
import com.ramble.ramblewallet.network.ApiRequest
import com.squareup.moshi.Json

class AppVersion @SuppressWarnings("unused") constructor() {

    @Json(name = "appSize")
    var appSize: Double? = 0.00

    @Json(name = "id")
    var id: Int? = -1

    @Json(name = "isForceUpdate")
    var isForceUpdate: Int? = -1//强制更新|0:否|1:是

    @Json(name = "isNeedUpdate")
    var isNeedUpdate: Int? = -1//状态|0:停用|1:启用

    @Json(name = "isPopPrompt")
    var isPopPrompt: Int? = -1//弹框提示|0:否|1:是

    @Json(name = "isVersionPrompt")
    var isVersionPrompt: Int? = -1//版本提示|0:否|1:是

    @Json(name = "updateContent")
    var updateContent: String? = ""//更新描述内容

    @Json(name = "updateUrl")
    var updateUrl: String? = ""//APP更新地址

    @Json(name = "version")
    var version: String? = ""//当前版本号

    class Req : ApiRequest.Body() {

        @Json(name = "appType")
        var appType: Int = 1//APP类型|1:android|2:ios
            private set

        @Json(name = "version")
        var version: String = BuildConfig.VERSION_NAME//当前版本号
            private set
    }
}

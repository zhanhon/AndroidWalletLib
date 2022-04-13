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
    var isForceUpdate: Int? = -1

    @Json(name = "isNeedUpdate")
    var isNeedUpdate: Int? = -1

    @Json(name = "isPopPrompt")
    var isPopPrompt: Int? = -1

    @Json(name = "isVersionPrompt")
    var isVersionPrompt: Int? = -1

    @Json(name = "updateContent")
    var updateContent: String? = ""

    @Json(name = "updateUrl")
    var updateUrl: String? = ""

    @Json(name = "version")
    var version: String? = ""

    class Req : ApiRequest.Body() {

        @Json(name = "appType")
        var appType: Int = 1
            private set

        @Json(name = "version")
        var version: String = BuildConfig.VERSION_NAME
            private set
    }
}

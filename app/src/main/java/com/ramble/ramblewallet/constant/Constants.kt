package com.ramble.ramblewallet.constant

import android.content.Context

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/13
 */

const val ARG_PARAM1 = "01"
const val ARG_PARAM2 = "02"
const val ARG_PARAM3 = "03"
const val ARG_PARAM4 = "04"
const val ARG_PARAM5 = "05"
const val ARG_PARAM6 = "06"
const val ARG_PARAM7 = "07"
const val ARG_PARAM8 = "08"
const val ARG_PARAM9 = "09"
const val ARG_PARAM10 = "10"
const val ARG_PARAM11 = "11"

//语言代码|zh_CN:简体中文|zh_TW:繁体中文|en:英文|th:泰语|vi:越南语
const val CN = "zh_CN"
const val TW = "zh_TW"
const val EN = "en"
const val LANGUAGE = "language"

const val APK_PACKAGE_ARCHIVE_TYPE = "application/vnd.android.package-archive"
lateinit var appContext: Context
    private set

var isDebug: Boolean = false
    private set

var appProcessName = "android"
    private set
var appUIDeep = 0
    private set

fun setupArchLibrary(
    __appContext: Context,
    __isDebug: Boolean,
    __appProcessName: String,
    __appUIDeep: Int
) {
    appContext = __appContext
    isDebug = __isDebug
    appProcessName = __appProcessName
    appUIDeep = __appUIDeep
}
const val WALLETINFO = "wallet_info"
const val RATEINFO = "rate_info"
const val ADDRESS_BOOK_INFO = "address_book_info"
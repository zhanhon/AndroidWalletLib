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
const val EN = "EN"
const val LANGUAGE = "language"
const val FIRSTLANGUAGE = "first_language"
const val DEVICE_TOKEN = "deviceToken"

const val CNY = "CNY"
const val HKD = "HKD"
const val USD = "USD"
const val CURRENCY = "currency"
const val CURRENCY_TRAN = "currency_tran"
const val READ_ID = "read_ID"
const val READ_ID_NEW = "read_ID_new"

const val TOKEN_INFO_NO = "token_info_no"
const val STATION_INFO = "station_info"

const val REQUEST_CODE_1025 = 1025
const val REQUEST_CODE_1029 = 1029

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
    mAppContext: Context,
    mIsDebug: Boolean,
    mAppProcessName: String,
    mAppUIDeep: Int
) {
    appContext = mAppContext
    isDebug = mIsDebug
    appProcessName = mAppProcessName
    appUIDeep = mAppUIDeep
}

const val WALLETINFO = "wallet_info"
const val WALLETSELECTED = "wallet_selected"
const val RATEINFO = "rate_info"
const val ADDRESS_BOOK_INFO = "address_book_info"
const val SELECTED_TOKENS = "selected_tokens"
const val IS_CONFIRM_MNEMONIC = "is_confirm_mnemonic"

/**
 * 是否设置指纹解锁key
 */
const val ISFINGERPRINT_KEY = "fingerprint_key"

/**
 * 是否设置指纹解锁key
 */
const val ISFINGERPRINT_KEY_ALL = "fingerprint_key_all"

/**
 * 是否设置指纹解锁key
 */
const val ISFINGERPRINT_KEY_COMMON = "fingerprint_key_common"
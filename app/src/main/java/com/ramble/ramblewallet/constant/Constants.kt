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
const val ARG_TITLE = "ARG_TITLE"


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
const val TOKEN_DB_NO = "token_db_no"
const val REQUEST_CODE_1025 = 1025
const val REQUEST_CODE_1029 = 1029
const val WALLETINFO = "wallet_info"
const val WALLETSELECTED = "wallet_selected"
const val ADDRESS_BOOK_INFO = "address_book_info"
const val SELECTED_TOKENS = "selected_tokens"
const val IS_CONFIRM_MNEMONIC = "is_confirm_mnemonic"
const val ISFINGERPRINT_KEY = "fingerprint_key" //是否设置指纹解锁key
const val ISFINGERPRINT_KEY_ALL = "fingerprint_key_all" //是否设置指纹解锁key
const val ISFINGERPRINT_KEY_COMMON = "fingerprint_key_common" //是否设置指纹解锁key

////////////网页/////////////
const val FORGOT_PASSWORD_EN = "/forgotPasswordEn.html"//忘记密码
const val FORGOT_PASSWORD_CN = "/forgotPassword.html"//忘记密码

////////////////////////币种类型//////////////////////////////
const val WALLET_TYPE_ETH = 1
const val WALLET_TYPE_TRX = 2
const val WALLET_TYPE_BTC = 3
const val WALLET_TYPE_SOL = 4
const val WALLET_TYPE_DOGE = 5

///
const val ATTR_HEADER_TOKEN = "access-token"
const val ATTR_LANGUES = "Accept-Language"
const val ATTR_CLIENT_TYPE = "client-type"

lateinit var appContext: Context
    private set

var isDebug: Boolean = false
    private set

var appProcessName = "android"
    private set
var appUIDeep = 0
    private set

lateinit var baseServerUrl:  MutableList<String>
    private set

lateinit var baseChatServerUrl:  String
    private set

fun setupArchLibrary(
    mAppContext: Context,
    mIsDebug: Boolean,
    mAppProcessName: String,
    mAppUIDeep: Int,
    serverUrl: MutableList<String>,
    chatServerUrl: String
) {
    appContext = mAppContext
    isDebug = mIsDebug
    appProcessName = mAppProcessName
    appUIDeep = mAppUIDeep
    baseServerUrl = serverUrl
    baseChatServerUrl = chatServerUrl
}

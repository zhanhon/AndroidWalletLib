package com.ramble.ramblewallet.utils

import android.app.Activity
import android.content.Context
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.constant.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间　: 2022/1/3 17:06
 * 作者　: potato
 * 描述　:
 */
object TimeUtils {

    /**
     * 字符串转时间，时间转周几
     *
     * @param dateString
     * @return
     */


    fun dateToWeek(activity: Activity, dateString: String?): String? {

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var data = sdf.parse(dateString)
        val weekDays = arrayOf(
            activity.getString(R.string.sunday),
            activity.getString(R.string.monday),
            activity.getString(R.string.tuesday),
            activity.getString(R.string.wednesday),
            activity.getString(R.string.thursday),
            activity.getString(R.string.friday),
            activity.getString(R.string.saturday)
        )
        val cal: Calendar = Calendar.getInstance()
        cal.time = data
        // 指示一个星期中的某天,0代表星期天。
        val w = if (cal.get(Calendar.DAY_OF_WEEK) - 1 < 0) 0 else cal.get(Calendar.DAY_OF_WEEK) - 1
        return weekDays[w]
    }

    /***
     * 转主链类型
     */

    fun dateToWalletType(date: Int?): String {
        return when (date) {
            1 -> {//ETH
                "ETH"
            }
            2 -> {//TRX
                "TRX"
            }
            3 -> {//btc
                "BTC"
            }
            4 -> {//btc
                "SOL"
            }
            5 -> {//btc
                "DOGE"
            }
            else -> "SOL"
        }
    }


    /***
     * 转主链类型
     */

    fun dateToWalletTypeInt(date: String?): Int {
        return when (date) {
            "ETH" -> 1
            "BTC" -> 3
            "TRX" -> 2
            "SOL" -> 4
            "DOGE" -> 5
            else -> 6
        }

    }

    /***
     * 转语言
     */

    fun dateToLang(context: Context): Int {
        return when (SharedPreferencesUtils.getString(context, LANGUAGE, CN)) {
            CN -> {
                1
            }
            TW -> {
                2
            }
            else -> {
                3
            }
        }
    }

    /***
     * 转货币
     */

    fun dateToCurrency(context: Context): Int {
        return when (SharedPreferencesUtils.getString(context, CURRENCY, USD)) {
            CNY -> {
                2
            }
            HKD -> {
                3
            }
            else -> {
                1
            }
        }
    }
}
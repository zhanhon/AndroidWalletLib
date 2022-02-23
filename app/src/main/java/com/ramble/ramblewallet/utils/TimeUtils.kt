package com.ramble.ramblewallet.utils

import android.app.Activity
import com.ramble.ramblewallet.R
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


}
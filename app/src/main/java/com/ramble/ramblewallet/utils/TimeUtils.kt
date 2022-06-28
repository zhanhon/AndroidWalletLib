package com.ramble.ramblewallet.utils

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.AllTokenBean
import com.ramble.ramblewallet.bean.MyAddressBean
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils
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
        return when (SharedPreferencesUtils.getSecurityString(context, LANGUAGE, CN)) {
            CN -> 1
            TW -> 2
            else -> 3
        }
    }

    /***
     * 转货币
     */

    fun dateToCurrency(context: Context): Int {
        return when (SharedPreferencesUtils.getSecurityString(context, CURRENCY, USD)) {
            CNY -> 2
            HKD -> 3
            else -> 1
        }
    }

    /***
     * 转货币
     */

    fun dateToAllTokenBean(date: String): AllTokenBean {
        val allToken = AllTokenBean()
        val myStores: ArrayList<StoreInfo> = arrayListOf()
        val r1 = StoreInfo()
        r1.id = 2396
        r1.symbol = "WETH"
        r1.contractAddress = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
        val r2 = StoreInfo()
        r2.id = 3717
        r2.symbol = "WBTC"
        r2.contractAddress = "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599"
        val r3 = StoreInfo()
        r3.id = 4943
        r3.symbol = "DAI"
        r3.contractAddress = "0x6b175474e89094c44da98b954eedeac495271d0f"
        val r4 = StoreInfo()
        r4.symbol = "USDC"
        r4.id = 3408
        r4.contractAddress = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"
        val r5 = StoreInfo()
        r5.symbol = "USDT"
        r5.id = 825
        r5.contractAddress = "0xdac17f958d2ee523a2206206994597c13d831ec7"
        r5.isMyToken = 2
        val r6 = StoreInfo()
        r6.symbol = "LINK"
        r6.id = 1975
        r6.contractAddress = "0x514910771af9ca656af840dff83e8264ecf986ca"
        val r7 = StoreInfo()
        r7.symbol = "YFI"
        r7.id = 5864
        r7.contractAddress = "0x0bc529c00c6401aef6d220be8c6ea1667f6ad93e"
        val r8 = StoreInfo()
        r8.symbol = "UNI"
        r8.id = 7083
        r8.contractAddress = "0x1f9840a85d5af5bf1d1762f925bdaddc4201f984"
        myStores.add(r5)
        myStores.add(r2)
        myStores.add(r3)
        myStores.add(r4)
        myStores.add(r8)
        myStores.add(r6)
        myStores.add(r1)
        myStores.add(r7)
        allToken.storeInfos = myStores
        allToken.myCurrency = date
        return allToken
    }

    /***
     * 转Type
     */

    fun dateToType(date: String): Int {
        return when {
            WalletBTCUtils.isBtcValidAddress(date) -> 2
            WalletETHUtils.isEthValidAddress(date) -> 1
            WalletTRXUtils.isTrxValidAddress(date) -> 3
            WalletSOLUtils.isSolValidAddress(date) -> 4
            else -> 6
        }
    }

    /***
     * 转Type
     */

    fun dateToTypeString(date: String): String {
        return when {
            WalletBTCUtils.isBtcValidAddress(date) -> "BTC01"
            WalletETHUtils.isEthValidAddress(date) -> "ETH01"
            WalletTRXUtils.isTrxValidAddress(date) -> "TRX01"
            WalletSOLUtils.isSolValidAddress(date) -> "SOL01"
            else -> "ETH01"
        }
    }

    /***
     * 转Type2
     */

    fun dateToTypeStringTwo(date: String, cout: Int): String {
        return when {
            WalletBTCUtils.isBtcValidAddress(date) -> "BTC" + String.format("%02d", cout)
            WalletETHUtils.isEthValidAddress(date) -> "ETH" + String.format("%02d", cout)
            WalletTRXUtils.isTrxValidAddress(date) -> "TRX" + String.format("%02d", cout)
            WalletSOLUtils.isSolValidAddress(date) -> "SOL" + String.format("%02d", cout)
            else -> ""
        }
    }

    /***
     * 转Type2
     */

    fun dateToNameString(date: String, nameUser: String, context: Context): String {
        var name: String? = null
        if (SharedPreferencesUtils.getSecurityString(
                context,
                ADDRESS_BOOK_INFO,
                ""
            )
                .isNotEmpty()
        ) {
            if (nameUser.isNotEmpty()) {
                name = nameUser
            } else {
                val myData: ArrayList<MyAddressBean> =
                    Gson().fromJson(
                        SharedPreferencesUtils.getSecurityString(
                            context,
                            ADDRESS_BOOK_INFO,
                            ""
                        ),
                        object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                    )
                val number = dateToType(date)
                var cout = 1
                myData.forEach {
                    if (it.type == number) {
                        cout++
                    }
                }
                name = dateToTypeStringTwo(date, cout)
            }
        } else {
            name = if (nameUser.isNotEmpty()) {
                nameUser
            } else {
                dateToTypeString(date)
            }
        }
        return name
    }

}


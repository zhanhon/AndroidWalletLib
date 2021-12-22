package com.ramble.ramblewallet.network

import com.ramble.ramblewallet.MyApp
import com.ramble.ramblewallet.utils.Pie
import com.tencent.mmkv.MMKV

/**
 * 时间　: 2021/12/22 18:57
 * 作者　: potato
 * 描述　:
 */
object MMKVManager {
    private val mmkv: MMKV by lazy {
        MMKV.defaultMMKV()
    }

    fun getDeviceId(): String {
        var value = mmkv.decodeString(Pie.MMKV_DEVICE_ID, "")
//        if (value.isEmpty()) {
//            value = try {
//                MyApp.INSTANCE.uniqueId()
//            } catch (e: Exception) {
//               getRandomUUID()
//            }
//            migrateDeviceId(value)
//        }
//        if (value.contains("-")) {
//            value = MyApp.INSTANCE.uniqueId()
//            mmkv.encode(Pie.MMKV_DEVICE_ID, value)
//        }
        return value
    }

    // 迁移老版本数据
    fun migrateDeviceId(deviceId: String) {
        if (mmkv.decodeString(Pie.MMKV_DEVICE_ID, "").isEmpty()) {
            if (deviceId.isNotEmpty()) {
                mmkv.encode(Pie.MMKV_DEVICE_ID, deviceId)
            }
        }
    }


}
package com.ramble.ramblewallet.network

import com.squareup.moshi.Json
import java.io.IOException

/**
 * 时间　: 2021/12/23 13:33
 * 作者　: potato
 * 描述　:
 */
interface Envelope<T> {
    fun status(): Boolean
    fun code(): Int
    fun message(): String
    fun data(): T?
}

package com.ramble.ramblewallet.network

import java.io.IOException
import java.util.ConcurrentModificationException
import java.util.NoSuchElementException
import java.util.concurrent.TimeoutException

/**
 * 时间　: 2021/12/21 14:34
 * 作者　: potato
 * 描述　:
 */
@JvmOverloads
fun <T> illegalArgumentException(message: String = "IllegalArgumentException"): T {
    throw IllegalArgumentException(message)
}


//未支持的业务,不允许直接抛出异常
@JvmOverloads
fun <T> unsupportedOperationException(message: String = "UnsupportedOperationException"): T? {
    //toastDefault("加载错误,请重试.")
    return null
//    throw UnsupportedOperationException(message)
}

@JvmOverloads
fun <T> assertionError(message: String = "AssertionError"): T {
    throw AssertionError(message)
}

fun assertTrue1(vararg integers: Int): Boolean {
    if (integers.isEmpty()) return false
    for (i in 0 until integers.size) {
        if (1 != integers[i]) {
            return false
        }
    }
    return true
}


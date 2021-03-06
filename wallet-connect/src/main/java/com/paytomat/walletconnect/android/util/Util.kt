package com.paytomat.walletconnect.android.util

/**
 * created by Alex Ivanov on 2019-06-05.
 */
inline fun <T> guard(func: () -> T): T? = try {
    func()
} catch (e: Exception) {
    null
}
package com.paytomat.walletconnect.android.model

/**
 * created by Alex Ivanov on 2019-06-13.
 */

const val JSONRPCVersion = "2.0"

data class JSONRPCError(val code: Int, val message: String)

data class JSONRPCRequest<T>(val id: Long, val method: String, val params: T, val jsonrpc: String = JSONRPCVersion)

data class JSONRPCResponse<T>(val id: Long, val result: T, val jsonrpc: String = JSONRPCVersion)

data class JSONRPCErrorResponse(val id: Long, val error: JSONRPCError, val jsonrpc: String = JSONRPCVersion)

public fun generateId(): Long {
    return System.currentTimeMillis()
}
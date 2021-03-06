package com.solana.api

import android.util.Base64
import com.solana.core.PublicKey
import com.solana.models.RPC
import com.solana.models.buffer.BufferInfo
import com.squareup.moshi.Types
import java.lang.reflect.Type

fun <T> Api.getAccountInfo(
    account: PublicKey,
    decodeTo: Class<T>,
    onComplete: ((Result<BufferInfo<T>>) -> Unit)
) {
    return getAccountInfo(account, HashMap(), decodeTo, onComplete)
}

fun <T> Api.getAccountInfo(
    account: PublicKey,
    additionalParams: Map<String, Any?>,
    decodeTo: Class<T>,
    onComplete: ((Result<BufferInfo<T>>) -> Unit)
) {


    val params: MutableList<Any> = ArrayList()
    val parameterMap: MutableMap<String, Any?> = HashMap()
    parameterMap["encoding"] = additionalParams.getOrDefault("encoding", "base64")
    params.add(account.toString())
    params.add(parameterMap)

    val type = Types.newParameterizedType(
        RPC::class.java,
        Types.newParameterizedType(
            BufferInfo::class.java,
            Type::class.java.cast(decodeTo)
        )
    )

    router.request<RPC<BufferInfo<T>>>("getAccountInfo", params, type) { result ->
        result
            .map {
                it.value!!
            }
            .onSuccess {
                onComplete(Result.success(it))
            }
            .onFailure {
                onComplete(Result.failure(it))
            }
    }
}


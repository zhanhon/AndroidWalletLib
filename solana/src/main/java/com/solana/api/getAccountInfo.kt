package com.solana.api

import com.google.gson.Gson
import com.solana.core.PublicKey
import com.solana.models.RPC
import com.solana.models.buffer.BufferInfo
import com.solana.models.buffer.BufferInfoJson
import com.solana.networking.models.RpcResponse
import com.squareup.moshi.Types
import java.lang.reflect.Type

fun <T>Api.getAccountInfo(account: PublicKey,
                                        decodeTo: Class<T>,
                                        onComplete: ((Result<BufferInfo<T>>) -> Unit)) {
    return getAccountInfo(account, HashMap(), decodeTo, onComplete)
}

fun <T> Api.getAccountInfo(account: PublicKey,
                          additionalParams: Map<String, Any?>,
                          decodeTo: Class<T>,
                          onComplete: ((Result<BufferInfo<T>>) -> Unit)) {


    val params: MutableList<Any> = ArrayList()
    val parameterMap: MutableMap<String, Any?> = HashMap()
    parameterMap["encoding"] = additionalParams.getOrDefault("encoding", "base64+zstd")
    params.add(account.toBase58())
    params.add(parameterMap)

    val type = Types.newParameterizedType(
        RPC::class.java,
        Types.newParameterizedType(
            BufferInfo::class.java,
            Type::class.java.cast(decodeTo)
        )
    )

    router.request<RPC<BufferInfoJson<T>>>("getAccountInfo", params, type) { result ->
        result
            .map {
                println("-=-=-=->transactionId：${Gson().toJson(it)}")
                it.value!!
            }
            .onSuccess {
                //onComplete(Result.success(it))
            }
            .onFailure {
                onComplete(Result.failure(it))
            }
    }
}


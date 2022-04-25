package com.solana.api

import com.solana.core.PublicKey
import com.solana.models.RPC
import com.solana.models.TokenResultObjects
import com.squareup.moshi.Types

fun Api.getTokenSupply(
    tokenMint: PublicKey,
    onComplete: (Result<TokenResultObjects.TokenAmountInfo>) -> Unit
) {
    val params: MutableList<Any> = ArrayList()
    params.add(tokenMint.toBase58())
    params.add(mapOf("encoding" to "base64", "commitment" to "recent"))
    val type = Types.newParameterizedType(
        RPC::class.java,
        TokenResultObjects.TokenAmountInfo::class.java
    )
    router.request<RPC<TokenResultObjects.TokenAmountInfo>>(
        "getTokenSupply",
        params,
        type
    ) { result ->
        result.map {
            it.value!!
        }.onSuccess {
            onComplete(Result.success(it))
        }.onFailure {
            onComplete(Result.failure(it))
        }
    }
}
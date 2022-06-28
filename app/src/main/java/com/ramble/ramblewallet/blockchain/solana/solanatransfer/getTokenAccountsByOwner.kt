package com.ramble.ramblewallet.blockchain.solana

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solana.api.Api
import com.solana.api.getTokenAccount
import com.solana.core.PublicKey
import com.solana.models.RPC
import com.solana.models.TokenAccountInfo
import com.squareup.moshi.Types

fun Api.getTokenAccountsByOwner(
    owner: PublicKey,
    tokenMint: PublicKey,
    onComplete: (Result<PublicKey>) -> Unit
) {
    val params: MutableList<Any> = ArrayList()
    params.add(owner.toBase58())
    val parameterMap: MutableMap<String, Any> = HashMap()
    parameterMap["mint"] = tokenMint.toBase58()
    params.add(parameterMap)
    params.add(mapOf("encoding" to "jsonParsed", "commitment" to "recent"))

    val type = Types.newParameterizedType(
        RPC::class.java,
        Types.newParameterizedType(
            List::class.java,
            Types.newParameterizedType(
                Map::class.java,
                String::class.java,
                Any::class.java
            )
        )
    )

    router.request<RPC<List<Map<String, Any>>>>(
        "getTokenAccountsByOwner", params,
        type
    ) { result ->
        result.map {
            if (it.value?.size!! > 0) {
                val jsonToList: List<TokenAccountsInfo> = Gson().fromJson(
                    it.value.toString(),
                    object : TypeToken<List<TokenAccountsInfo>>() {}.type
                )
                var resultValue = "111111"
                for (index in jsonToList.indices) {
                    if (jsonToList[index].account.data.parsed.info.tokenAmount.uiAmountString != "0") {
                        resultValue = it.value!![index]["pubkey"] as String
                    }
                }
                resultValue
            } else {
                "111111"
            }
        }.map {
            PublicKey(it)
        }.onSuccess {
            onComplete(Result.success(it))
        }.onFailure {
            onComplete(Result.failure(it))
        }
    }
}

fun Api.getTokenAccountsByOwner(
    accountOwner: PublicKey, requiredParams: Map<String, Any>,
    optionalParams: Map<String, Any>?,
    onComplete: (Result<TokenAccountInfo>) -> Unit,
) {
    getTokenAccount(
        accountOwner,
        requiredParams,
        optionalParams,
        "getTokenAccountsByOwner",
        onComplete
    )
}
package com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc

import android.util.Base64
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.Transaction
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionAccount
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionPublicKey
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.types.RecentBlockhash
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.types.RpcResultTypes
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.types.config.Commitment
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.types.config.RpcSendTransactionConfig
import kotlinx.serialization.builtins.serializer


/**
 * Created by guness on 6.12.2021 21:47
 */
class RpcApi(private val client: RpcClient) {

    @Throws(RpcException::class)
    fun getRecentBlockhash(commitment: Commitment? = null): String? {
        val params = mutableListOf<Any>()

        commitment?.let {
            params.add(mapOf("commitment" to it.value))
        }

        return client.call(
            "getRecentBlockhash",
            params,
            RecentBlockhash.serializer()
        ).value?.blockhash
    }

    @Throws(RpcException::class)
    fun sendTransaction(
        transaction: Transaction,
        signer: TransactionAccount,
        recentBlockHash: String? = null
    ): String {
        return sendTransaction(transaction, listOf(signer), recentBlockHash)
    }

    @Throws(RpcException::class)
    fun sendTransaction(
        transaction: Transaction,
        signers: List<TransactionAccount>,
        recentBlockHash: String?
    ): String {

        val blockhash = recentBlockHash ?: getRecentBlockhash()

        transaction.setRecentBlockHash(blockhash)
        transaction.sign(signers)

        val serializedTransaction = transaction.serialize()
        val base64Trx = Base64.encodeToString(serializedTransaction, Base64.NO_WRAP)
        val params = listOf(base64Trx, RpcSendTransactionConfig())

        return client.call("sendTransaction", params, String.serializer())
    }

    @Throws(RpcException::class)
    fun getBalance(account: TransactionPublicKey, commitment: Commitment? = null): Long {

        val params = mutableListOf<Any>()
        params.add(account.toString())

        commitment?.let {
            params.add(mapOf("commitment" to it.value))
        }
        return client.call("getBalance", params, RpcResultTypes.ValueLong.serializer()).value
    }
}
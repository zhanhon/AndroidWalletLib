package com.ramble.ramblewallet.blockchain.solana.solanatransfer.core

/**
 * Created by guness on 6.12.2021 16:17
 */
data class AccountMeta(
    val publicKey: TransactionPublicKey,
    val signer: Boolean,
    val writable: Boolean
)

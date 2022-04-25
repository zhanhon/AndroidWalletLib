package com.ramble.ramblewallet.blockchain.solana

data class TokenAccountsInfo(
    val account: Account,
    val publicKey: String
)

data class Account(
    val data: Data,
    val executable: Boolean,
    val lamports: Long,
    val owner: String,
    val rentEpoch: Long
)

data class Data(
    val parsed: Parsed,
    val program: String,
    val space: Long
)

data class Parsed(
    val info: Info,
    val type: String
)

data class Info(
    val tokenAmount: TokenAmount,
    val isNative: Boolean,
    val mint: String,
    val owner: String,
    val state: String
)

data class TokenAmount(
    val amount: String,
    val decimals: Int,
    val uiAmount: Double,
    val uiAmountString: String
)
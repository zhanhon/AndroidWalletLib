package com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.types

import kotlinx.serialization.Serializable

/**
 * Created by guness on 6.12.2021 21:50
 */
@Serializable
open class RpcResultObject(val context: Context? = null) {
    @Serializable
    class Context(val slot: Long = 0)
}

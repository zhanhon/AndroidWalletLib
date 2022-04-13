package com.ramble.ramblewallet.blockchain.solana.solanatransfer.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

/**
 * Created by guness on 6.12.2021 20:54
 */
val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    serializersModule = SerializersModule { }
}
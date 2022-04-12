package com.ramble.ramblewallet.blockchain.solana.solanasdk.core

import com.ramble.ramblewallet.blockchain.solana.solanasdk.utils.PublicKeyValidator
import org.bitcoinj.core.Base58

class PublicKey {
    private var publicKey: ByteArray

    constructor(publicKey: ByteArray) {
        PublicKeyValidator.validate(publicKey)
        this.publicKey = publicKey
    }

    fun asByteArray(): ByteArray {
        return publicKey.copyOf()
    }

    fun toBase58(): String {
        return Base58.encode(publicKey)
    }

    fun equals(pubkey: PublicKey): Boolean {
        return this.publicKey.contentEquals(pubkey.asByteArray())
    }

    override fun toString(): String {
        return toBase58()
    }
}

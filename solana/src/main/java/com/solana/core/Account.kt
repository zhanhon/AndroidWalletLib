package com.solana.core

import com.solana.vendor.TweetNaclFast
import com.solana.vendor.bip32.wallet.DerivableType
import com.solana.vendor.bip32.wallet.SolanaBip44
import org.bitcoinj.crypto.DeterministicHierarchy
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.HDUtils
import org.bitcoinj.crypto.MnemonicCode
import java.nio.ByteBuffer
import java.util.*

class Account {
    private var keyPair: TweetNaclFast.Signature.KeyPair

    constructor() {
        keyPair = TweetNaclFast.Signature.keyPair()
    }

    constructor(secretKey: ByteArray) {
        keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(secretKey)
    }

    private constructor(keyPair: TweetNaclFast.Signature.KeyPair) {
        this.keyPair = keyPair
    }

    val publicKey: PublicKey
        get() = PublicKey(keyPair.publicKey)
    val secretKey: ByteArray
        get() = keyPair.secretKey

    companion object {

        fun fromBip44Mnemonic(
            words: List<String>,
            passphrase: String = ""
        ): Account {
            val solanaBip44 = SolanaBip44()
            val seed = MnemonicCode.toSeed(words, passphrase)
            val privateKey = solanaBip44.getPrivateKeyFromSeed(seed, DerivableType.BIP44CHANGE)
            return Account(TweetNaclFast.Signature.keyPair_fromSeed(privateKey))
        }

        /**
         * Creates an [Account] object from a Sollet-exported JSON string (array)
         * @param json Sollet-exported JSON string (array)
         * @return [Account] built from Sollet-exported private key
         */
        fun fromJson(json: String): Account {
            return Account(convertJsonStringToByteArray(json))
        }

        /**
         * Convert's a Sollet-exported JSON string into a byte array usable for [Account] instantiation
         * @param characters Sollet-exported JSON string
         * @return byte array usable in [Account] instantiation
         */
        private fun convertJsonStringToByteArray(characters: String): ByteArray {
            // Create resulting byte array
            val buffer = ByteBuffer.allocate(64)

            // Convert json array into String array
            val sanitizedJson = characters.replace("\\[".toRegex(), "").replace("]".toRegex(), "")
            val chars = sanitizedJson.split(",").toTypedArray()

            // Convert each String character into byte and put it in the buffer
            Arrays.stream(chars).forEach { character: String ->
                val byteValue = character.toInt().toByte()
                buffer.put(byteValue)
            }
            return buffer.array()
        }
        fun privateKeyToWallet(secretKey: ByteArray): Account {
            return Account(TweetNaclFast.Signature.keyPair_fromSecretKey(secretKey))
        }
    }
}
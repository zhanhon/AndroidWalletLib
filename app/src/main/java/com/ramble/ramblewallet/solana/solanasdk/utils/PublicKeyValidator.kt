package com.ramble.ramblewallet.solana.solanasdk.utils

object PublicKeyValidator {
    private const val REGEX = "[1-9A-HJ-NP-Za-km-z]{32,44}"

    @Throws(IllegalArgumentException::class)
    fun validate(publicKey: String) {
        require(Regex(REGEX).matches(publicKey) && Base58Utils.decode(publicKey).size == 32) {
            "Invalid public key input[String]"
        }
    }

    @Throws(IllegalArgumentException::class)
    fun validate(publicKey: ByteArray) {
        require(Regex(REGEX).matches(Base58Utils.encode(publicKey)) && publicKey.size == 32) {
            "Invalid public key input[ByteArray]"
        }
    }
}

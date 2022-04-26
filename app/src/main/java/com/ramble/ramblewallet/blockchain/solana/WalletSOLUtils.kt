package com.ramble.ramblewallet.blockchain.solana

import android.util.Base64
import com.ramble.ramblewallet.bean.Wallet
import com.solana.core.Account
import com.solana.core.Account.Companion.privateKeyToWallet
import io.github.novacrypto.base58.Base58
import org.komputing.kbase58.encodeToBase58String

class WalletSOLUtils {
    companion object {
        /**
         * 通过助记词生成钱包
         *
         * @param walletname
         * @param walletPassword
         * @param mnemonic
         * @return
         */
        fun generateWalletByMnemonic(
            walletname: String,
            walletPassword: String,
            mnemonic: String,
            mnemonicList: List<String>
        ): Wallet {
            return try {
                var passphrase: ArrayList<String> = mnemonic.split(" ") as ArrayList<String>
                val account = Account.fromBip44Mnemonic(passphrase)
                val address = account.publicKey.toBase58()
                val privateKey = account.secretKey.encodeToBase58String()
                Wallet(
                    walletname,
                    walletPassword,
                    mnemonic,
                    address,
                    privateKey,
                    null,
                    4,
                    mnemonicList
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Wallet("", "", "", "", "", "", 4, null)
            }
        }

        /**
         * 通过privateKey生成钱包
         *
         * @param walletname
         * @param walletPassword
         * @param privateKey
         * @return
         */
        fun generateWalletByPrivateKey(
            walletname: String,
            walletPassword: String,
            privateKey: String,
            mnemonicList: List<String>
        ): Wallet {
            return try {
                val account = privateKeyToWallet(Base58.base58Decode(privateKey))
                val address = account.publicKey.toBase58()
                Wallet(walletname, walletPassword, null, address, privateKey, null, 4, mnemonicList)
            } catch (e: Exception) {
                e.printStackTrace()
                Wallet("", "", "", "", "", "", 4, null)
            }
        }

        fun isSolValidAddress(address: String): Boolean {
            if (address.isEmpty()) {
                return false
            }
            return true
        }
    }
}
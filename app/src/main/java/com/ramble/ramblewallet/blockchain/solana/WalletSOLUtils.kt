package com.ramble.ramblewallet.blockchain.solana

import android.util.Base64
import android.util.Log
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.blockchain.solana.solanasdk.core.Account
import com.solana.core.Account.Companion.privateKeyToWallet

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
//                var passphrase: ArrayList<String> =
//                    mnemonic.split(" ").toTypedArray() as ArrayList<String>
                val passphrase: ArrayList<String> = arrayListOf()
                passphrase.add("ranch")
                passphrase.add("slight")
                passphrase.add("close")
                passphrase.add("cart")
                passphrase.add("venture")
                passphrase.add("trip")
                passphrase.add("minute")
                passphrase.add("repeat")
                passphrase.add("cute")
                passphrase.add("utility")
                passphrase.add("cotton")
                passphrase.add("rhythm")
                val account = Account.fromBip44Mnemonic(
                    passphrase,
                    Base64.DEFAULT
                )
                val address = account.publicKey.toBase58()
                val privateKey = Base64.encodeToString(account.secretKey, Base64.NO_WRAP)
                Log.v("-=-=->privateKey:", privateKey)
                Log.v("-=-=->address:", address)
                Wallet(
                    walletname,
                    walletPassword,
                    mnemonic,
                    account.publicKey.toBase58(),
                    privateKey,
                    null,
                    null,
                    4,
                    mnemonicList
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Wallet("", "", "", "", "", "", "", 4, null)
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
                val account = privateKeyToWallet(Base64.decode(privateKey, Base64.DEFAULT))
                val address = account.publicKey.toBase58()
                Log.v("-=-=->address:", address)
                Wallet(
                    walletname,
                    walletPassword,
                    null,
                    address,
                    privateKey,
                    null,
                    null,
                    4,
                    mnemonicList
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Wallet("", "", "", "", "", "", "", 4, null)
            }
        }

        fun isSolValidAddress(address: String): Boolean {
            if (address.isEmpty()) {
                return false
            }
            return address.length == 44
        }
    }
}
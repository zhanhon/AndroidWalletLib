package com.ramble.ramblewallet.solana

import android.util.Base64
import android.util.Log
import com.ramble.ramblewallet.bean.Wallet

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
                val account = org.p2p.solanaj.core.Account.fromBip44MnemonicWithChange(
                    passphrase,
                    Base64.DEFAULT
                )
                val address = account.publicKey.toBase58()
                val privateKey = Base64.encodeToString(account.secretKey, Base64.DEFAULT)
                Log.v("-=-=-=->privateKey:", privateKey)
                Log.v("-=-=-=->address:", address)
                Wallet(
                    walletname,
                    walletPassword,
                    mnemonic,
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
    }
}
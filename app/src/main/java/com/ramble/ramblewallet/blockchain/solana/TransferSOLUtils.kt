package com.ramble.ramblewallet.blockchain.solana

import android.util.Log
import com.guness.ksolana.core.Account
import com.guness.ksolana.core.PublicKey
import com.guness.ksolana.core.Transaction
import com.guness.ksolana.programs.SystemProgram.transfer
import com.guness.ksolana.rpc.RpcApi
import com.guness.ksolana.rpc.RpcClient


object TransferSOLUtils {

    private val api by lazy { RpcApi(RpcClient("https://api.testnet.solana.com")) }


    fun loadBalance() {
        Thread {
          val balance = api.getBalance(PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")) / 1000000000f
          println("-=-=-=->balance：${balance}")
          //balanceView.text = "$balance SOL"
        }.start()
    }

    fun makeTransfer() {
        loadBalance()
        Thread  {
            val fromPublicKey = PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")
            val toPublicKey = PublicKey("6nPn5BmREMctaS37B2zz3Veb92GujaUif7o1uNtUY42d")
            val lamports = 100000000 //需要乘上10九次方   100000000 = 0.1

            val signer = Account("36AYNyF9rpTqKahAvtUwyverakCJ2Lae3p1uWyKcVYxcB43tgrA6uYLFmS1bPyUDCn7Pn5TJnwUVBuUa5kRUZ4VG")

            val transaction = Transaction()
            transaction.addInstruction(transfer(fromPublicKey, toPublicKey, lamports.toLong()))

            val signature = api.sendTransaction(transaction, signer)
            Log.d("kSolana", "signature: $signature")
            Thread.sleep(4000)
            loadBalance()
        }.start()
    }
}
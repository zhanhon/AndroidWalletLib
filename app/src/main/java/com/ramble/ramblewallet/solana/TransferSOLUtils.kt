package com.ramble.ramblewallet.solana

import android.util.Log
import com.ramble.ramblewallet.utils.postUI



object TransferSOLUtils {
//
//    private val api by lazy { RpcApi(RpcClient("https://api.testnet.solana.com")) }
//
//
//    private fun loadBalance() {
//        Thread {
//            //lifecycleScope.launch {
//          val balance = api.getBalance(PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")) / 1000000000f
//                postUI {
//                    //balanceView.text = "$balance SOL"
//                }
//            //}
//        }.start()
//    }
//
//    private fun makeTransfer() {
//        loadBalance()
//        Thread  {
//            val fromPublicKey = PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")
//            val toPublicKey = PublicKey("6nPn5BmREMctaS37B2zz3Veb92GujaUif7o1uNtUY42d")
//            val lamports = 100000000 //需要乘上10九次方   100000000 = 0.1
//
//            val signer = Account("36AYNyF9rpTqKahAvtUwyverakCJ2Lae3p1uWyKcVYxcB43tgrA6uYLFmS1bPyUDCn7Pn5TJnwUVBuUa5kRUZ4VG")
//
//            val transaction = Transaction()
//            transaction.addInstruction(transfer(fromPublicKey, toPublicKey, lamports.toLong()))
//
//            val signature = api.sendTransaction(transaction, signer)
//            Log.d("kSolana", "signature: $signature")
//            Thread.sleep(4000)
//            loadBalance()
//        }.start()
//    }
}
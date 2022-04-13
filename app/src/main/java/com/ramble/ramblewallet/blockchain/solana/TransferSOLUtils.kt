package com.ramble.ramblewallet.blockchain.solana

import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.Transaction
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionAccount
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionPublicKey
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.programs.SystemProgram.transfer
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.RpcApi
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.RpcClient


object TransferSOLUtils {

    private val api by lazy { RpcApi(RpcClient("https://api.testnet.solana.com")) }


    fun loadBalance() {
        Thread {
            val balance =
                api.getBalance(TransactionPublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")) / 1000000000f
            Thread.sleep(2000)
            println("-=-=-=->balance：${balance}")
        }.start()
    }

    fun makeTransfer() {
        Thread {
            val fromPublicKey = TransactionPublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")
            val toPublicKey = TransactionPublicKey("6nPn5BmREMctaS37B2zz3Veb92GujaUif7o1uNtUY42d")
            val lamports = 1000000 //需要乘上10九次方   100000000 = 0.1

            val signer =
                TransactionAccount("36AYNyF9rpTqKahAvtUwyverakCJ2Lae3p1uWyKcVYxcB43tgrA6uYLFmS1bPyUDCn7Pn5TJnwUVBuUa5kRUZ4VG")

            val transaction = Transaction()
            transaction.addInstruction(transfer(fromPublicKey, toPublicKey, lamports.toLong()))

            val signature = api.sendTransaction(transaction, signer)
            println("-=-=-=->signature：${signature}")
            Thread.sleep(4000)
        }.start()
    }
}
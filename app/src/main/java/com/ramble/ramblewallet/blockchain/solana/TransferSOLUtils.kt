package com.ramble.ramblewallet.blockchain.solana

import com.google.gson.Gson
import com.solana.Solana
import com.solana.actions.Action
import com.solana.actions.sendSPLTokens
import com.solana.api.*
import com.solana.core.Account
import com.solana.core.DerivationPath
import com.solana.core.PublicKey
import com.solana.models.TokenResultObjects
import com.solana.networking.NetworkingRouter
import com.solana.networking.RPCEndpoint
import com.solana.programs.SystemProgram
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import org.json.JSONObject
import java.util.*


object TransferSOLUtils {

    fun getBalance() {
        Thread {
            val solana =
                Solana(NetworkingRouter(RPCEndpoint.mainnetBetaSolana), InMemoryAccountStorage())
            val balance =
                solana.api.getBalance(PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci"))
                    .blockingGet() / 1000000000f
            println("-=-=-=->balance：${balance}")
        }.start()
    }

    fun getTokenBalance() {
        Thread {
            val solana =
                Solana(NetworkingRouter(RPCEndpoint.mainnetBetaSolana), InMemoryAccountStorage())
            val result = solana.api.getTokenAccountsByOwner(
                PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci"),
                PublicKey("Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB")
            ).blockingGet()
            val balanceJson = solana.api.getTokenAccountBalance(result).blockingGet()
            val json = JSONObject(Gson().toJson(balanceJson))
            val balance = json.optString("uiAmountString")
            println("-=-=-=->balance：${balance}")
        }.start()
    }

    fun sendSOL() {
        Thread {
            val solana =
                Solana(NetworkingRouter(RPCEndpoint.mainnetBetaSolana), InMemoryAccountStorage())
            val lamports = 1000000L //需要乘上10九次方   1000000 = 0.001
            val destination = PublicKey("6nPn5BmREMctaS37B2zz3Veb92GujaUif7o1uNtUY42d")
            val feePayer: Account = Account.fromMnemonic(
                Arrays.asList(
                    "ranch",
                    "slight",
                    "close",
                    "cart",
                    "venture",
                    "trip",
                    "minute",
                    "repeat",
                    "cute",
                    "utility",
                    "cotton",
                    "rhythm"
                ), "", DerivationPath.BIP44_M_44H_501H_0H_OH
            )
            val instructions = SystemProgram.transfer(feePayer.publicKey, destination, lamports)
            val transaction = com.solana.core.Transaction()
            transaction.addInstruction(instructions)
            val transactionHash =
                solana.api.sendTransaction(transaction, listOf(feePayer)).blockingGet()
            println("-=-=-=->transactionHash：${transactionHash}")
        }.start()
    }

    fun sendSPLTokens() { //接口结构有变，未成功
        Thread {
            val solana =
                Solana(NetworkingRouter(RPCEndpoint.mainnetBetaSolana), InMemoryAccountStorage())
            val feePayer: Account = Account.fromMnemonic(
                Arrays.asList(
                    "ranch",
                    "slight",
                    "close",
                    "cart",
                    "venture",
                    "trip",
                    "minute",
                    "repeat",
                    "cute",
                    "utility",
                    "cotton",
                    "rhythm"
                ), "", DerivationPath.BIP44_M_44H_501H_0H_OH
            )
            val source = PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")
            val destination = PublicKey("6nPn5BmREMctaS37B2zz3Veb92GujaUif7o1uNtUY42d")
            val mintAddress = PublicKey("Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB")
            val transactionId = solana.action.sendSPLTokens(
                feePayer,
                mintAddress = mintAddress,
                fromPublicKey = source,
                destinationAddress = destination,
                1000L
            ).blockingGet()
            println("-=-=-=->transactionId：${Gson().toJson(transactionId)}")
        }.start()
    }


    private fun Api.getBalance(account: PublicKey): Single<Long> {
        return Single.create { emitter ->
            this.getBalance(account) { result ->
                result.onSuccess {
                    emitter.onSuccess(it)
                }.onFailure {
                    emitter.onError(it)
                }
            }
            Disposables.empty()
        }
    }

    private fun Api.getTokenAccountsByOwner(address: PublicKey, tokenMint: PublicKey): Single<PublicKey> {
        return Single.create { emitter ->
            this.getTokenAccountsByOwner(address, tokenMint) { result ->
                result.onSuccess {
                    emitter.onSuccess(it)
                }.onFailure {
                    emitter.onError(it)
                }
            }
            Disposables.empty()
        }
    }

    private fun Api.getTokenAccountBalance(tokenMint: PublicKey): Single<TokenResultObjects.TokenAmountInfo> {
        return Single.create { emitter ->
            this.getTokenAccountBalance(tokenMint) { result ->
                result.onSuccess {
                    emitter.onSuccess(it)
                }.onFailure {
                    emitter.onError(it)
                }
            }
            Disposables.empty()
        }
    }

    private fun Api.sendTransaction(
        transaction: com.solana.core.Transaction,
        signer: List<Account>
    ): Single<String> {
        return Single.create { emitter ->
            this.sendTransaction(transaction, signer) { result ->
                result.onSuccess {
                    emitter.onSuccess(it)
                }.onFailure {
                    emitter.onError(it)
                }
            }
            Disposables.empty()
        }
    }

    private fun Action.sendSPLTokens(
        account: Account,
        mintAddress: PublicKey,
        fromPublicKey: PublicKey,
        destinationAddress: PublicKey,
        amount: Long
    ): Single<String> {
        return Single.create { emitter ->
            this.sendSPLTokens(
                mintAddress = mintAddress,
                fromPublicKey = fromPublicKey,
                destinationAddress = destinationAddress,
                amount = amount,
                account = account
            ) { result ->
                result.onSuccess {
                    emitter.onSuccess(it)
                }.onFailure {
                    emitter.onError(it)
                }
            }
            Disposables.empty()
        }
    }

    //private val api by lazy { RpcApi(RpcClient("https://api.testnet.solana.com")) }
    //这是另外获取余额方式
    //val balance = api.getBalance(TransactionPublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")) / 1000000000f
    //另外一种SOL转账方式
    //val fromPublicKey = TransactionPublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")
    //val toPublicKey = TransactionPublicKey("6nPn5BmREMctaS37B2zz3Veb92GujaUif7o1uNtUY42d")
    //val lamports = 1000000 //需要乘上10九次方   100000000 = 0.1
    //val signer = TransactionAccount("36AYNyF9rpTqKahAvtUwyverakCJ2Lae3p1uWyKcVYxcB43tgrA6uYLFmS1bPyUDCn7Pn5TJnwUVBuUa5kRUZ4VG")
    //val transaction = Transaction()
    //transaction.addInstruction(transfer(fromPublicKey, toPublicKey, lamports.toLong()))
    //val signature = api.sendTransaction(transaction, signer)
    //println("-=-=-=->signature：${signature}")
    //Thread.sleep(4000)
}
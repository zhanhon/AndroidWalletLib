package com.ramble.ramblewallet.blockchain.solana

import android.app.Activity
import android.os.Build
import android.webkit.WebView
import com.google.gson.Gson
import com.ramble.ramblewallet.activity.MainSOLActivity
import com.ramble.ramblewallet.activity.TransferActivity
import com.solana.Solana
import com.solana.actions.Action
import com.solana.actions.sendSPLTokens
import com.solana.api.*
import com.solana.core.Account
import com.solana.core.PublicKey
import com.solana.models.TokenAccountInfo
import com.solana.models.TokenResultObjects
import com.solana.networking.NetworkingRouter
import com.solana.networking.RPCEndpoint
import com.solana.programs.SystemProgram
import io.github.novacrypto.base58.Base58
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.properties.Delegates


object TransferSOLUtils {

    fun getSOLBalance(context: Activity, address: String) {
        Thread {
            var balance by Delegates.notNull<Float>()
            try {
                val solana =
                    Solana(NetworkingRouter(RPCEndpoint.mainnetBetaSolana), InMemoryAccountStorage())
                balance = solana.api.getBalance(PublicKey(address)).blockingGet() / 1000000000f
                if (context is MainSOLActivity) {
                    context.setSolBalance(BigDecimal(balance.toString()))
                }
            } catch (e: Exception) {
                if (context is MainSOLActivity) {
                    context.setSolBalance(BigDecimal(balance.toString()))
                }
                e.printStackTrace()
            }
        }.start()
    }

    fun getsSOLTokenBalance(context: Activity, address: String, contractAddress: String) {
        Thread {
            var balance by Delegates.notNull<String>()
            try {
                val solana =
                    Solana(NetworkingRouter(RPCEndpoint.mainnetBetaSolana), InMemoryAccountStorage())
                val result = solana.api.getTokenAccountsByOwner(
                    PublicKey(address),
                    PublicKey(contractAddress)
                ).blockingGet()
                if (result != PublicKey("111111")) {
                    val balanceJson = solana.api.getTokenAccountBalance(result).blockingGet()
                    val json = JSONObject(Gson().toJson(balanceJson))
                    balance = json.optString("uiAmountString")
                    println("-=-=-=->balance：${balance}")
                    if (context is MainSOLActivity) {
                        context.setSolTokenBalance(BigDecimal(balance))
                    }
                }
            } catch (e: Exception) {
                if (context is MainSOLActivity) {
                    context.setSolTokenBalance(BigDecimal(balance))
                }
                e.printStackTrace()
            }
        }.start()
    }

    fun transferSOL(context: Activity, toAddress: String, privateKey: String?, amount: BigInteger) {
        Thread {
            try {
                val account = Account.privateKeyToWallet(Base58.base58Decode(privateKey))
                val solana =
                    Solana(
                        NetworkingRouter(RPCEndpoint.mainnetBetaSolana),
                        InMemoryAccountStorage()
                    )
                val instructions =
                    SystemProgram.transfer(account.publicKey, PublicKey(toAddress), amount.toLong())
                val transaction = com.solana.core.Transaction()
                transaction.addInstruction(instructions)
                val transactionHash =
                    solana.api.sendTransaction(transaction, listOf(account)).blockingGet()
                println("-=-=-=->transactionHash：${transactionHash}")
                if (context is TransferActivity) {
                    context.transferSuccess(transactionHash, null)
                }
            } catch (e: Exception) {
                if (context is TransferActivity) {
                    context.transferFail(e.message!!)
                }
                e.printStackTrace()
            }
        }.start()
    }

    fun transferSOLToken() { //接口结构有变，未成功
        Thread {
            val solana =
                Solana(NetworkingRouter(RPCEndpoint.mainnetBetaSolana), InMemoryAccountStorage())
            val privateKey = "36AYNyF9rpTqKahAvtUwyverakCJ2Lae3p1uWyKcVYxcB43tgrA6uYLFmS1bPyUDCn7Pn5TJnwUVBuUa5kRUZ4VG";
            val account = Account.privateKeyToWallet(Base58.base58Decode(privateKey))
            val source = PublicKey("Ch4CJs1bFL9bftBYbpWacar8o9sX9ALWeBgxizYxfLci")
            val destination = PublicKey("6nPn5BmREMctaS37B2zz3Veb92GujaUif7o1uNtUY42d")
            val mintAddress = PublicKey("Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB")
            val transactionId = solana.action.sendSPLTokens(
                account,
                mintAddress = mintAddress,
                fromPublicKey = source,
                destinationAddress = destination,
                1000L
            ).blockingGet()
            println("-=-=-=->transactionId：${Gson().toJson(transactionId)}")
        }.start()
    }

    //允许跨域请求
    fun setAllowUniversalAccessFromFileURLs(mwebview: WebView) {
        try { //本地HTML里面有跨域的请求 原生webview需要设置之后才能实现跨域请求
            if (Build.VERSION.SDK_INT >= 16) {
                val clazz: Class<*> = mwebview.settings.javaClass
                val method = clazz.getMethod(
                    "setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType
                )
                if (method != null) {
                    method.invoke(mwebview.settings, true)
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    fun Api.getTokenAccountsByDelegate(
        accountDelegate: PublicKey,
        requiredParams: Map<String, Any>,
        optionalParams: Map<String, Any>?,
        onComplete: (Result<TokenAccountInfo>) -> Unit
    ) {
        return getTokenAccount(
            accountDelegate,
            requiredParams,
            optionalParams,
            "getTokenAccountsByDelegate",
            onComplete
        )
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

    private fun Api.getTokenAccountsByOwner(
        address: PublicKey,
        tokenMint: PublicKey
    ): Single<PublicKey> {
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
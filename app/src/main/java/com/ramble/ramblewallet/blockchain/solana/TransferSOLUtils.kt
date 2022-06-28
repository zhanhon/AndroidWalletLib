package com.ramble.ramblewallet.blockchain.solana

import android.webkit.WebView
import com.google.gson.Gson
import com.ramble.ramblewallet.BuildConfig.RPC_SOL_NODE
import com.ramble.ramblewallet.blockchain.IBalanceListener
import com.ramble.ramblewallet.blockchain.ITransferListener
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.Transaction
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionAccount
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionPublicKey
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.programs.SystemProgram.transfer
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.RpcApi
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.rpc.RpcClient
import com.solana.api.Api
import com.solana.api.getBalance
import com.solana.api.getTokenAccountBalance
import com.solana.core.PublicKey
import com.solana.models.TokenResultObjects
import com.solana.networking.NetworkingRouter
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import java.net.URL


object TransferSOLUtils {

    private val node = RPC_SOL_NODE
    fun getSOLBalance(address: String,iBalanceListener: IBalanceListener) {
        var balance = BigDecimal(0)
        try {
            val api = Api(NetworkingRouter(URL(node)))
            val balanceOrigin = api.getBalance(PublicKey(address)).blockingGet()
            balance = BigDecimal(balanceOrigin.toString()).divide(BigDecimal("10").pow(9))
            iBalanceListener.onBalance(balance)
        } catch (e: Exception) {
            iBalanceListener.onBalance(balance)
            e.printStackTrace()
        }
    }

    fun getsSOLTokenBalance(address: String, contractAddress: String,iBalanceListener: IBalanceListener) {
        var balance = "0"
        try {
            val api = Api(NetworkingRouter(URL(node)))
            val result = api.getTokenAccountsByOwner(
                PublicKey(address),
                PublicKey(contractAddress)
            ).blockingGet()
            if (result != PublicKey("111111")) {
                val balanceJson = api.getTokenAccountBalance(result).blockingGet()
                val json = JSONObject(Gson().toJson(balanceJson))
                balance = json.optString("uiAmountString")
                iBalanceListener.onBalance(BigDecimal(balance))
            }
        } catch (e: Exception) {
            iBalanceListener.onBalance(BigDecimal(balance))
            e.printStackTrace()
        }
    }

    fun transferSOL(
        fromAddress: String,
        toAddress: String,
        privateKey: String,
        amount: BigDecimal,
        iTransferListener: ITransferListener
    ) {
        Thread {
            try {
                val api by lazy { RpcApi(RpcClient(node)) }
                val fromPublicKey = TransactionPublicKey(fromAddress)
                val toPublicKey = TransactionPublicKey(toAddress)
                val signer = TransactionAccount(privateKey)
                val transaction = Transaction()
                transaction.addInstruction(
                    transfer(
                        fromPublicKey,
                        toPublicKey,
                        amount.longValueExact()
                    )
                )
                val transactionHash = api.sendTransaction(transaction, signer)
                iTransferListener.onTransferSuccess(transactionHash, null)
            } catch (e: Exception) {
                iTransferListener.onTransferFail(e.message!!)
                e.printStackTrace()
            }
        }.start()
    }

    //允许跨域请求
    fun setAllowUniversalAccessFromFileURLs(mwebview: WebView) {
        try { //本地HTML里面有跨域的请求 原生webview需要设置之后才能实现跨域请求
            val clazz: Class<*> = mwebview.settings.javaClass
            val method = clazz.getMethod(
                "setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType
            )
            if (method != null) {
                method.invoke(mwebview.settings, true)
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

}
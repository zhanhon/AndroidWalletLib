package com.ramble.ramblewallet.activity

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paytomat.walletconnect.android.Status
import com.paytomat.walletconnect.android.WCCallbacks
import com.paytomat.walletconnect.android.WCInteractor
import com.paytomat.walletconnect.android.model.WCBinanceOrder
import com.paytomat.walletconnect.android.model.WCPeerMeta
import com.paytomat.walletconnect.android.model.WCSession
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity

import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.ActivityWalletConnectBinding
import com.ramble.ramblewallet.helper.getExtras
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 时间　: 2022/5/2 15:43
 * 作者　: potato
 * 描述　:
 */
class WalletConnectActivity : BaseActivity(), WCCallbacks, View.OnClickListener{
    private lateinit var binding: ActivityWalletConnectBinding
    private var interactor: WCInteractor? = null
    private lateinit var walletSelleted: Wallet
    private var wc = ""
    private val handler: Handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_connect)

        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        wc = getExtras().getString(ARG_PARAM1, "")
        binding.headerLogo.setOnClickListener {
            onWalletConnect(wc)
        }
    }


    private fun onWalletConnect(result: String) {
        if (interactor != null) {
            interactor?.killSession()
            interactor = null
        } else {
            val sessionStr: String = result
            val clientMeta = WCPeerMeta(
                getString(R.string.app_name),
                "https://github.com/TrustWallet/wallet-connect-swift"
            )
            val session: WCSession = WCSession.fromURI(sessionStr) ?: return

            //Use Prefs instead
            interactor = WCInteractor(
                session,
                clientMeta,
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            )
            interactor?.callbacks = this@WalletConnectActivity
            interactor?.connect()
        }
    }

    override fun onStart() {
        super.onStart()
        interactor?.connect()
    }

    override fun onStop() {
        super.onStop()
        interactor?.disconnect()
    }

    private fun initListener() {
        binding.tvCancel.setOnClickListener(this)
        binding.tvConfirm.setOnClickListener(this)
    }

    override fun onStatusUpdate(status: Status) {
        handler.post {
            when (status) {
                Status.DISCONNECTED -> "Disconnected"
                Status.FAILED_CONNECT -> "Failed to connect"
                Status.CONNECTING -> "Connecting"
                Status.CONNECTED -> "Connected"
            }

//            screen_main_connect_button.isEnabled = status != Status.CONNECTING
//            screen_main_connect_button.text = if (status == Status.CONNECTED) "Disconnect" else "Connect"
        }
    }

    override fun onSessionRequest(id: Long, peer: WCPeerMeta) {
        binding.headerName.text = peer.name + " 请求连接到你的钱包"
        binding.headerUrl.text = peer.url
//        binding.headerLogo.setImageURI(peer.icons.first().toUri())
        initListener()
//        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//            .setMessage("Confirm session with ${peer.name}")
//            .setPositiveButton("Confirm") { _, _ ->
//                interactor?.approveSession(
//                    arrayOf(walletSelleted.address),
//                    1
//                )
//            }.setNegativeButton("Reject") { _, _ ->
//                interactor?.rejectSession()
//                interactor?.killSession()
//            }
        handler.post { }
    }

    override fun onBnbSign(id: Long, order: WCBinanceOrder<*>) {
        handler.post {
            AlertDialog.Builder(this)
                .setMessage(Gson().toJson(order))
                .setPositiveButton("ok") { _, _ ->
//                    val orderJson: String = GsonBuilder().serializeNulls().create().toJson(order)
//                    val signature: ByteArray = Signature.signMessage(orderJson.toByteArray(), privateKey)
//                    val signed = WCBinanceOrderSignature(
//                        Hex.toHexString(signature),
//                        Hex.toHexString(privateKey.publicKey.bytes)
//                    )
//                    interactor?.approveBnbOrder(id, signed)
                }.setNegativeButton("no") { _, _ -> interactor?.rejectRequest(id, "Rejected") }
                .show()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_cancel -> {
                interactor?.rejectSession()
                interactor?.killSession()
            }
            R.id.tv_confirm -> {
                interactor?.approveSession(
                    arrayOf(walletSelleted.address),
                    1
                )
            }
        }
    }

}
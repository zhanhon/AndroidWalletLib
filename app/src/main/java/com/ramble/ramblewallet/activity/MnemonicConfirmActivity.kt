package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MnemonicConfirmAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils.isBtcValidAddress
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils.Companion.isSolValidAddress
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.custom.AutoLineFeedLayoutManager
import com.ramble.ramblewallet.databinding.ActivityMnemonicConfirmBinding
import com.ramble.ramblewallet.fragment.*
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*


class MnemonicConfirmActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mnemonicConfirmAdapter: MnemonicConfirmAdapter
    private lateinit var binding: ActivityMnemonicConfirmBinding
    private lateinit var walletSelleted: Wallet
    private lateinit var mnemonicDoubleList: List<String>
    private lateinit var currentTab: String
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private var mnemonicETHShuffled: ArrayList<String> = arrayListOf()
    private var mnemonicETHOriginal: ArrayList<String> = arrayListOf()
    private var mnemonicETHChoose: ArrayList<String> = arrayListOf()
    private var walletETHString: String = ""
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var putAddressTimes = 0
    private var clickErrorCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mnemonic_confirm)
        walletSelleted = intent.getSerializableExtra(ARG_PARAM1) as Wallet
        mnemonicDoubleList = intent.getStringArrayListExtra(ARG_PARAM2)!!
        currentTab = intent.getStringExtra(ARG_PARAM3)!!
        initClick()
        initData()
    }

    fun updateListData(walletTemp: Wallet){
        val wallet: Wallet =
            Gson().fromJson(
                SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
                object : TypeToken<Wallet>() {}.type
            )
        if (walletTemp.address == wallet.address){
            wallet.isBackupAlready = true
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(wallet))
        }

        val walletList = SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "")
        if (walletList.isNotEmpty()) {
            val walletList: ArrayList<Wallet> = Gson().fromJson(walletList, object : TypeToken<ArrayList<Wallet>>() {}.type)
            walletList.forEach {
                if (walletTemp.address == it.address){
                    it.isBackupAlready = true
                }
            }
            SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(walletList))
        }


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_english -> {
                currentTab = "english"
                initData()
            }
            R.id.ll_chinese -> {
                currentTab = "chinese"
                initData()
            }
            R.id.btn_mnemonic_completed -> {
                if (mnemonicETHChoose == mnemonicETHOriginal) {
                    if (!walletSelleted.mnemonic.isNullOrEmpty()) {
                        SharedPreferencesUtils.saveSecurityBoolean(this, IS_CONFIRM_MNEMONIC, true)
                        if (walletSelleted.address.isNotEmpty()) {
                            walletSelleted.isBackupAlready = true
                            updateListData(walletSelleted)
                        }
                        ActivityManager.getAppManager().finishAllActivity()
                    } else {
                        createWallet()
                    }
                } else {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.confirm_contributing_words_error)
                    )
                }
            }
            R.id.tv_mnemonic_name_1 -> {
                mnemonicNameClick(0, binding.tvMnemonicName1)
            }
            R.id.tv_mnemonic_name_2 -> {
                mnemonicNameClick(1, binding.tvMnemonicName2)
            }
            R.id.tv_mnemonic_name_3 -> {
                mnemonicNameClick(2, binding.tvMnemonicName3)
            }
            R.id.tv_mnemonic_name_4 -> {
                mnemonicNameClick(3, binding.tvMnemonicName4)
            }
            R.id.tv_mnemonic_name_5 -> {
                mnemonicNameClick(4, binding.tvMnemonicName5)
            }
            R.id.tv_mnemonic_name_6 -> {
                mnemonicNameClick(5, binding.tvMnemonicName6)
            }
            R.id.tv_mnemonic_name_7 -> {
                mnemonicNameClick(6, binding.tvMnemonicName7)
            }
            R.id.tv_mnemonic_name_8 -> {
                mnemonicNameClick(7, binding.tvMnemonicName8)
            }
            R.id.tv_mnemonic_name_9 -> {
                mnemonicNameClick(8, binding.tvMnemonicName9)
            }
            R.id.tv_mnemonic_name_10 -> {
                mnemonicNameClick(9, binding.tvMnemonicName10)
            }
            R.id.tv_mnemonic_name_11 -> {
                mnemonicNameClick(10, binding.tvMnemonicName11)
            }
            R.id.tv_mnemonic_name_12 -> {
                mnemonicNameClick(11, binding.tvMnemonicName12)
            }
        }
    }

    private fun initClick() {
        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnMnemonicCompleted.setOnClickListener(this)
        binding.tvMnemonicName1.setOnClickListener(this)
        binding.tvMnemonicName2.setOnClickListener(this)
        binding.tvMnemonicName3.setOnClickListener(this)
        binding.tvMnemonicName4.setOnClickListener(this)
        binding.tvMnemonicName5.setOnClickListener(this)
        binding.tvMnemonicName6.setOnClickListener(this)
        binding.tvMnemonicName7.setOnClickListener(this)
        binding.tvMnemonicName8.setOnClickListener(this)
        binding.tvMnemonicName9.setOnClickListener(this)
        binding.tvMnemonicName10.setOnClickListener(this)
        binding.tvMnemonicName11.setOnClickListener(this)
        binding.tvMnemonicName12.setOnClickListener(this)
    }

    private fun createWallet() {
        if (DoubleUtils.isFastDoubleClick()) return
        mnemonicETHOriginal.forEach {
            walletETHString = "$walletETHString$it "
        }
        val walletList = SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "")
        if (walletList.isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(walletList, object : TypeToken<ArrayList<Wallet>>() {}.type)
        }
        when (walletSelleted.walletType) {
            1 -> { //ETH
                ethLogicHandle()
            }
            2 -> { //TRON
                trxLogicHandle()
            }
            3 -> { //BTC
                btcLogicHandle()
            }
            4 -> { //SOL
                solLogicHandle()
            }
            100 -> { //ALL
                allLogicHandle()
            }
        }
    }

    private fun allLogicHandle() {
        val walletETH = WalletETHUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        val walletSOL = WalletSOLUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        walletETH.isIdWallet = true
        walletTRX.isIdWallet = true
        walletBTC.isIdWallet = true
        walletSOL.isIdWallet = true
        if ((!Gson().toJson(saveWalletList).contains(walletETH.address))
            && (!Gson().toJson(saveWalletList).contains(walletBTC.address))
            && (!Gson().toJson(saveWalletList).contains(walletTRX.address))
            && (!Gson().toJson(saveWalletList).contains(walletSOL.address))
        ) {
            allLogicHandleSub(walletETH, walletTRX, walletBTC, walletSOL)
            if (saveWalletList.isEmpty()) {
                walletSOL.index = 0
            } else {
                walletSOL.index = saveWalletList[saveWalletList.size - 1].index + 1
            }
            saveWalletList.add(walletSOL)
            walletTRX.index = saveWalletList[saveWalletList.size - 1].index + 1
            saveWalletList.add(walletTRX)
            walletBTC.index = saveWalletList[saveWalletList.size - 1].index + 1
            saveWalletList.add(walletBTC)
            walletETH.index = saveWalletList[saveWalletList.size - 1].index + 1
            saveWalletList.add(walletETH)
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
            detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 3))
            detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
            detailsList.add(AddressReport.DetailsList(walletSOL.address, 0, 4))
            //2、之后地址校验
            val isValidEthSuccess = isEthValidAddress(walletETH.address)
            val isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
            val isValidBtcSuccess = isBtcValidAddress(walletBTC.address)
            val isValidSolSuccess = isSolValidAddress(walletSOL.address)
            if (isValidEthSuccess && isValidTrxSuccess && isValidBtcSuccess && isValidSolSuccess) {
                putAddress(detailsList)
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                //设置选择默认
                SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletETH))
                RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainETHFragment::class.toString())
            }
        }
    }

    private fun allLogicHandleSub(
        walletETH: Wallet,
        walletTRX: Wallet,
        walletBTC: Wallet,
        walletSOL: Wallet,
    ) {
        if (walletSelleted.walletName.isEmpty()) {
            var index1 = 1
            var index2 = 1
            var index3 = 1
            var index4 = 1
            walletETH.walletName = "ETH" + String.format("%02d", index1)
            walletTRX.walletName = "TRX" + String.format("%02d", index2)
            walletBTC.walletName = "BTC" + String.format("%02d", index3)
            walletSOL.walletName = "SOL" + String.format("%02d", index4)
            if (saveWalletList.isNotEmpty()) {
                saveWalletList.forEach {
                    if (it.walletName == walletETH.walletName) {
                        index1++
                    }
                    if (it.walletName == walletTRX.walletName) {
                        index2++
                    }
                    if (it.walletName == walletBTC.walletName) {
                        index3++
                    }
                    if (it.walletName == walletBTC.walletName) {
                        index4++
                    }
                }
            }
            walletETH.walletName = "ETH" + String.format("%02d", index1)
            walletTRX.walletName = "TRX" + String.format("%02d", index2)
            walletBTC.walletName = "BTC" + String.format("%02d", index3)
            walletSOL.walletName = "SOL" + String.format("%02d", index4)
        }
    }

    private fun solLogicHandle() {
        val walletSOL = WalletSOLUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        if (!Gson().toJson(saveWalletList).contains(walletSOL.address)) {
            if (walletSelleted.walletName.isEmpty()) {
                var index = 1
                walletSOL.walletName = "SOL" + String.format("%02d", index)
                if (saveWalletList.isNotEmpty()) {
                    saveWalletList.forEach {
                        if (it.walletName == walletSOL.walletName) {
                            index++
                        }
                    }
                }
                walletSOL.walletName = "SOL" + String.format("%02d", index)
            }
            walletSOL.index = saveWalletList[0].index + 1
            saveWalletList.add(walletSOL)
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            detailsList.add(AddressReport.DetailsList(walletSOL.address, 0, 3))
            val isValidSolSuccess = isSolValidAddress(walletSOL.address)
            if (isValidSolSuccess) {
                putAddress(detailsList)
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletSOL))
                RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainSOLFragment::class.toString())
            }
        }
    }

    private fun btcLogicHandle() {
        val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        if (!Gson().toJson(saveWalletList).contains(walletBTC.address)) {
            if (walletSelleted.walletName.isEmpty()) {
                var index = 1
                walletBTC.walletName = "BTC" + String.format("%02d", index)
                if (saveWalletList.isNotEmpty()) {
                    saveWalletList.forEach {
                        if (it.walletName == walletBTC.walletName) {
                            index++
                        }
                    }
                }
                walletBTC.walletName = "BTC" + String.format("%02d", index)
            }
            walletBTC.index = saveWalletList[0].index + 1
            saveWalletList.add(walletBTC)
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 3))
            val isValidBtcSuccess = isBtcValidAddress(walletBTC.address)
            if (isValidBtcSuccess) {
                putAddress(detailsList)
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletBTC))
                RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainBTCFragment::class.toString())
            }
        }
    }

    private fun trxLogicHandle() {
        val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        if (!Gson().toJson(saveWalletList).contains(walletTRX.address)) {
            if (walletSelleted.walletName.isEmpty()) {
                var index = 1
                walletTRX.walletName = "TRX" + String.format("%02d", index)
                if (saveWalletList.isNotEmpty()) {
                    saveWalletList.forEach {
                        if (it.walletName == walletTRX.walletName) {
                            index++
                        }
                    }
                }
                walletTRX.walletName = "TRX" + String.format("%02d", index)
            }
            walletTRX.index = saveWalletList[0].index + 1
            saveWalletList.add(walletTRX)
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
            val isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
            if (isValidTrxSuccess) {
                putAddress(detailsList)
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletTRX))
                RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE, MainTRXFragment::class.toString())
            }
        }
    }

    private fun ethLogicHandle() {
        val walletETH = WalletETHUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicDoubleList[0],
            mnemonicDoubleList
        )
        if (!Gson().toJson(saveWalletList).contains(walletETH.address)) {
            if (walletSelleted.walletName.isEmpty()) {
                var index = 1
                walletETH.walletName = "ETH" + String.format("%02d", index)
                if (saveWalletList.isNotEmpty()) {
                    saveWalletList.forEach {
                        if (it.walletName == walletETH.walletName) {
                            index++
                        }
                    }
                }
                walletETH.walletName = "ETH" + String.format("%02d", index)
            }
            walletETH.index = saveWalletList[0].index + 1
            saveWalletList.add(walletETH)
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
            val isValidEthSuccess = isEthValidAddress(walletETH.address)
            if (isValidEthSuccess) {
                putAddress(detailsList)
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletETH))
                RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainETHFragment::class.toString())
            }
        }
    }

    private fun initData() {
        myDataBeans.clear()
        mnemonicETHChoose.clear()
        when (currentTab) {
            "english" -> {
                binding.vChinese.visibility = View.INVISIBLE
                binding.vEnglish.visibility = View.VISIBLE
                binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
                mnemonicETHOriginal = mnemonicDoubleList[0].split(" ") as ArrayList<String>
                mnemonicETHShuffled = mnemonicDoubleList[0].split(" ") as ArrayList<String>
                mnemonicETHShuffled.shuffle()
            }
            "chinese" -> {
                binding.vEnglish.visibility = View.INVISIBLE
                binding.vChinese.visibility = View.VISIBLE
                binding.vChinese.setBackgroundResource(R.color.color_3F5E94)
                mnemonicETHOriginal = mnemonicDoubleList[1].split(" ") as ArrayList<String>
                mnemonicETHShuffled = mnemonicDoubleList[1].split(" ") as ArrayList<String>
                mnemonicETHShuffled.shuffle()
            }
        }
        binding.tvMnemonicName1.text = mnemonicETHShuffled[0]
        binding.tvMnemonicName2.text = mnemonicETHShuffled[1]
        binding.tvMnemonicName3.text = mnemonicETHShuffled[2]
        binding.tvMnemonicName4.text = mnemonicETHShuffled[3]
        binding.tvMnemonicName5.text = mnemonicETHShuffled[4]
        binding.tvMnemonicName6.text = mnemonicETHShuffled[5]
        binding.tvMnemonicName7.text = mnemonicETHShuffled[6]
        binding.tvMnemonicName8.text = mnemonicETHShuffled[7]
        binding.tvMnemonicName9.text = mnemonicETHShuffled[8]
        binding.tvMnemonicName10.text = mnemonicETHShuffled[9]
        binding.tvMnemonicName11.text = mnemonicETHShuffled[10]
        binding.tvMnemonicName12.text = mnemonicETHShuffled[11]
        binding.tvMnemonicConfirmTips.visibility = View.VISIBLE
        binding.tvMnemonicName1.visibility = View.VISIBLE
        binding.tvMnemonicName2.visibility = View.VISIBLE
        binding.tvMnemonicName3.visibility = View.VISIBLE
        binding.tvMnemonicName4.visibility = View.VISIBLE
        binding.tvMnemonicName5.visibility = View.VISIBLE
        binding.tvMnemonicName6.visibility = View.VISIBLE
        binding.tvMnemonicName7.visibility = View.VISIBLE
        binding.tvMnemonicName8.visibility = View.VISIBLE
        binding.tvMnemonicName9.visibility = View.VISIBLE
        binding.tvMnemonicName10.visibility = View.VISIBLE
        binding.tvMnemonicName11.visibility = View.VISIBLE
        binding.tvMnemonicName12.visibility = View.VISIBLE
    }

    private fun mnemonicFailDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_contributing_words_fail)
            dialogCenterTheme(window)
            if (walletSelleted.address.isNotEmpty()) {
                window.findViewById<TextView>(R.id.tv_content).setText(R.string.error_tips_backup)
                window.findViewById<Button>(R.id.btn_cancel_create).setText(R.string.cancel)
                window.findViewById<Button>(R.id.btn_skip).visibility = View.GONE
            } else {
                window.findViewById<TextView>(R.id.tv_content).setText(R.string.error_tips)
                window.findViewById<TextView>(R.id.tv_content).visibility = View.VISIBLE
                window.findViewById<Button>(R.id.btn_cancel_create).setText(R.string.cancel_create)
                window.findViewById<Button>(R.id.btn_skip).visibility = View.VISIBLE
            }
            window.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                dialog.dismiss()
            }
            window.findViewById<Button>(R.id.btn_cancel_create).setOnClickListener {
                if (walletSelleted.address.isNotEmpty()) {
                    SharedPreferencesUtils.saveSecurityBoolean(this, IS_CONFIRM_MNEMONIC, false)
                    finish()
                } else {
                    when (walletSelleted.walletType) {
                        1 -> {
                            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainETHFragment::class.toString())
                        }
                        2 -> {
                            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainTRXFragment::class.toString())
                        }
                        3 -> {
                            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainBTCFragment::class.toString())
                        }
                        4 -> {
                            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,CreateRecoverWalletFragment::class.toString())
                        }
                    }
                }
                dialog.dismiss()
                finish()
            }
            window.findViewById<Button>(R.id.btn_skip).setOnClickListener {
                createWallet()
                dialog.dismiss()
            }
            window.findViewById<Button>(R.id.btn_back_last_page).setOnClickListener {
                finish()
                dialog.dismiss()
            }
        }
    }

    private fun dialogCenterTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun mnemonicNameClick(index: Int, tvMnemonicName: TextView) {
        myDataBeans.forEach {
            if (it.isWrong) {
                return
            }
        }
        tvMnemonicName.visibility = View.INVISIBLE
        binding.tvMnemonicConfirmTips.visibility = View.GONE
        myDataBeans.add(MyDataBean(index, tvMnemonicName.text.toString(), false))
        mnemonicETHChoose.add(tvMnemonicName.text.toString())
        mnemonicETHChoose.forEachIndexed { index, element ->
            if (element != mnemonicETHOriginal[index]) {
                if (clickErrorCount >= 3) {
                    mnemonicFailDialog()
                }
                myDataBeans[index].isWrong = true
                clickErrorCount++
            }
        }
        mnemonicConfirmAdapter = MnemonicConfirmAdapter(myDataBeans)
        binding.rvMnemonic.layoutManager = AutoLineFeedLayoutManager()
        binding.rvMnemonic.adapter = mnemonicConfirmAdapter
        mnemonicConfirmClick(tvMnemonicName)
    }

    private fun mnemonicConfirmClick(tvMnemonicName: TextView) {
        binding.btnMnemonicCompleted.isEnabled = myDataBeans.size == 12
        mnemonicConfirmAdapter.setOnItemClickListener { _, _, position ->
            tvMnemonicName.visibility = View.VISIBLE
            mnemonicETHChoose.remove(mnemonicETHChoose[position])
            mnemonicConfirmAdapter.remove(myDataBeans[position])
            mnemonicConfirmAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("CheckResult")
    private fun putAddress(detailsList: ArrayList<AddressReport.DetailsList>) {
        val languageCode = SharedPreferencesUtils.getSecurityString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getSecurityString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    if (putAddressTimes < 3) {
                        putAddress(detailsList)
                        putAddressTimes++
                    }
                }
            }, {
            }
        )
    }
}
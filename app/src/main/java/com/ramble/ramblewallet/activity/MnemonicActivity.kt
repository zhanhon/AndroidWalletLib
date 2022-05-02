package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MnemonicAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils.isBtcValidAddress
import com.ramble.ramblewallet.blockchain.ethereum.MnemonicUtils
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils.Companion.isSolValidAddress
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMnemonicBinding
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.DoubleUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo


class MnemonicActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMnemonicBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var contributingWordsAdapter: MnemonicAdapter
    private lateinit var mnemonicETH: ArrayList<String>
    private lateinit var mnemonicList: List<String>
    private lateinit var walletName: String
    private lateinit var walletPassword: String
    private lateinit var currentTab: String
    private var walletETHString: String = ""
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var walletType = 1 //链类型|1:ETH|2:TRX|3:BTC|4:SOL|5:DOGE|100:BTC、ETH、TRX、SOL、DOGE
    private var isBackupMnemonic = false
    private var mnemonic: String? = null
    private var fromMnemonicList: ArrayList<String>? = arrayListOf()
    private var putAddressTimes = 0

    companion object {
        @JvmField
        var instance: MnemonicActivity = MnemonicActivity()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mnemonic)
        walletName = intent.getStringExtra(ARG_PARAM1).toString()
        walletPassword = intent.getStringExtra(ARG_PARAM2).toString()
        walletType = intent.getIntExtra(ARG_PARAM3, 1)
        isBackupMnemonic = intent.getBooleanExtra(ARG_PARAM4, false)
        mnemonic = intent.getStringExtra(ARG_PARAM5)
        fromMnemonicList = intent.getStringArrayListExtra(ARG_PARAM6)

        initClick()
    }

    override fun onResume() {
        super.onResume()
        currentTab = "english"//默认英文
        binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
        binding.vChinese.visibility = View.INVISIBLE
        if ((mnemonic != null) && (fromMnemonicList != null)) {
            mnemonicList = fromMnemonicList as ArrayList<String>
        } else {
            when (SharedPreferencesUtils.getSecurityString(this, LANGUAGE, CN)) {
                EN, CN -> {
                    mnemonicList = MnemonicUtils.generateMnemonicEnglishChinese()
                }
                TW -> {
                    mnemonicList = MnemonicUtils.generateMnemonicChineseTraditional()
                }
            }
        }
        if (isBackupMnemonic) {
            binding.btnSkipThis.visibility = View.GONE
        } else {
            binding.btnSkipThis.visibility = View.VISIBLE
        }
        createContributingWordsPage(mnemonicList[0])
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_english -> {
                binding.vChinese.visibility = View.INVISIBLE
                binding.vEnglish.visibility = View.VISIBLE
                binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
                createContributingWordsPage(mnemonicList[0])
                currentTab = "english"
            }
            R.id.ll_chinese -> {
                binding.vEnglish.visibility = View.INVISIBLE
                binding.vChinese.visibility = View.VISIBLE
                binding.vChinese.setBackgroundResource(R.color.color_3F5E94)
                createContributingWordsPage(mnemonicList[1])
                currentTab = "chinese"
            }
            R.id.btn_one_copy -> {
                when (currentTab) {
                    "english" -> {
                        ClipboardUtils.copy(mnemonicList[0], this)
                    }
                    "chinese" -> {
                        ClipboardUtils.copy(mnemonicList[1], this)
                    }
                    else -> {
                        ClipboardUtils.copy(mnemonicList[0], this)
                    }
                }
            }
            R.id.btn_skip_this -> {
                skipConfirmHandle()
            }
        }
    }

    private fun initClick() {
        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnOneCopy.setOnClickListener(this)
        binding.btnSkipThis.setOnClickListener(this)
    }

    private fun createContributingWordsPage(mnemonicString: String) {
        myDataBeans.clear()
        mnemonicETH = mnemonicString.split(" ") as ArrayList<String>
        mnemonicETH.forEachIndexed { index, element ->
            myDataBeans.add(MyDataBean(index + 1, element, false))
        }
        contributingWordsAdapter =
            MnemonicAdapter(myDataBeans)
        binding.rvContributingWords.adapter = contributingWordsAdapter
        binding.btnContributingWordsConfirm.setOnClickListener {
            startActivity(Intent(this, MnemonicConfirmActivity::class.java).apply {
                putStringArrayListExtra(ARG_PARAM1, ArrayList(mnemonicList))
                putExtra(ARG_PARAM2, walletName)
                putExtra(ARG_PARAM3, walletPassword)
                putExtra(ARG_PARAM4, currentTab)
                putExtra(ARG_PARAM5, walletType)
                putExtra(ARG_PARAM6, isBackupMnemonic)
                putExtra(ARG_PARAM7, mnemonic)
            })
        }
    }

    private fun skipConfirmHandle() {
        if (DoubleUtils.isFastDoubleClick()) return
        when (currentTab) {
            "english" -> {
                walletETHString = mnemonicList[0]
            }
            "chinese" -> {
                walletETHString = mnemonicList[1]
            }
        }
        val walletList = SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "")
        if (walletList.isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(walletList, object : TypeToken<ArrayList<Wallet>>() {}.type)
        }
        when (walletType) {
            1 -> { //ETH
                ethLogicHandle()
            }
            2 -> { //TRX
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
        var walletETH = WalletETHUtils.generateWalletByMnemonic(
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        val walletSOL = WalletSOLUtils.generateWalletByMnemonic(
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
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
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
        detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 3))
        detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
        detailsList.add(AddressReport.DetailsList(walletSOL.address, 0, 4))
        //2、之后地址校验
        var isValidEthSuccess = isEthValidAddress(walletETH.address)
        var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
        var isValidBtcSuccess = isBtcValidAddress(walletBTC.address)
        val isValidSolSuccess = isSolValidAddress(walletSOL.address)
        if (isValidEthSuccess && isValidTrxSuccess && isValidBtcSuccess && isValidSolSuccess) {
            putAddress(detailsList)
            //设置选择默认
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletETH))
            startActivity(Intent(this, MainETHActivity::class.java))
        }
    }

    private fun allLogicHandleSub(
        walletETH: Wallet,
        walletTRX: Wallet,
        walletBTC: Wallet,
        walletSOL: Wallet,
    ) {
        if (walletName.isEmpty()) {
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
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        if (walletName.isEmpty()) {
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
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletSOL.address, 0, 3))
        var isValidSolSuccess = isSolValidAddress(walletSOL.address)
        if (isValidSolSuccess) {
            putAddress(detailsList)
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletSOL))
            startActivity(Intent(this, MainSOLActivity::class.java))
        }
    }

    private fun btcLogicHandle() {
        val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        if (walletName.isEmpty()) {
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
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 3))
        var isValidBtcSuccess = isBtcValidAddress(walletBTC.address)
        if (isValidBtcSuccess) {
            putAddress(detailsList)
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletBTC))
            startActivity(Intent(this, MainBTCActivity::class.java))
        }
    }

    private fun trxLogicHandle() {
        val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        if (walletName.isEmpty()) {
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
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
        var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
        if (isValidTrxSuccess) {
            putAddress(detailsList)
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletTRX))
            startActivity(Intent(this, MainTRXActivity::class.java))
        }
    }

    private fun ethLogicHandle() {
        var walletETH = WalletETHUtils.generateWalletByMnemonic(
            walletName,
            walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        if (walletName.isEmpty()) {
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
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
        var isValidEthSuccess = isEthValidAddress(walletETH.address)
        if (isValidEthSuccess) {
            putAddress(detailsList)
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletETH))
            startActivity(Intent(this, MainETHActivity::class.java))
        }
    }

    @SuppressLint("CheckResult")
    private fun putAddress(detailsList: ArrayList<AddressReport.DetailsList>) {
        val languageCode = SharedPreferencesUtils.getSecurityString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getSecurityString(appContext, DEVICE_TOKEN, "")
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() != 1) {
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
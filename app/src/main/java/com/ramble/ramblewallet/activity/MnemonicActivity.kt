package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.ContributingWordsAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils.isBtcValidAddress
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityContributingWordsBinding
import com.ramble.ramblewallet.ethereum.MnemonicUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo


class MnemonicActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityContributingWordsBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var contributingWordsAdapter: ContributingWordsAdapter
    private lateinit var mnemonicETH: ArrayList<String>
    private lateinit var mnemonicList: ArrayList<String>
    private lateinit var walletName: String
    private lateinit var walletPassword: String
    private var currentTab = "english"
    private var walletETHString: String = ""
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var walletType = 1 //链类型|1:ETH|2:TRX|3:BTC|4：BTC、ETH、TRX
    private var isBackupMnemonic = false
    private var mnemonic: String? = null
    private var fromMnemonicList: ArrayList<String>? = arrayListOf()

    companion object {
        @JvmField
        var instance: MnemonicActivity = MnemonicActivity()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words)
        walletName = intent.getStringExtra(ARG_PARAM1)
        walletPassword = intent.getStringExtra(ARG_PARAM2)
        walletType = intent.getIntExtra(ARG_PARAM3, 1)
        isBackupMnemonic = intent.getBooleanExtra(ARG_PARAM4, false)
        mnemonic = intent.getStringExtra(ARG_PARAM5)
        fromMnemonicList = intent.getStringArrayListExtra(ARG_PARAM6)

        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnOneCopy.setOnClickListener(this)
        binding.btnSkipThis.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        currentTab = "english"//默认英文
        binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
        binding.vChinese.visibility = View.INVISIBLE
        if ((mnemonic != null) && (fromMnemonicList != null)) {
            mnemonicList = fromMnemonicList as ArrayList<String>
        } else {
            // 生成钱包助记词
            when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
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
                        ClipboardUtils.copy(mnemonicList[0])
                    }
                    "chinese" -> {
                        ClipboardUtils.copy(mnemonicList[1])
                    }
                    else -> {
                        ClipboardUtils.copy(mnemonicList[0])
                    }
                }
            }
            R.id.btn_skip_this -> {
                skipConfirmHandle()
            }
        }
    }

    private fun createContributingWordsPage(mnemonicString: String) {
        myDataBeans.clear()
        mnemonicETH = mnemonicString.split(" ") as ArrayList<String>
        mnemonicETH.forEachIndexed { index, element ->
            myDataBeans.add(MyDataBean(index + 1, element, false))
        }
        contributingWordsAdapter = ContributingWordsAdapter(myDataBeans)
        binding.rvContributingWords.adapter = contributingWordsAdapter
        binding.btnContributingWordsConfirm.setOnClickListener {
            startActivity(Intent(this, MnemonicConfirmActivity::class.java).apply {
                putStringArrayListExtra(ARG_PARAM1, mnemonicList)
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
        when (currentTab) {
            "english" -> {
                walletETHString = mnemonicList[0]
            }
            "chinese" -> {
                walletETHString = mnemonicList[1]
            }
        }

        when (walletType) {
            1 -> { //ETH
                var walletETH = WalletETHUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim(),
                    mnemonicList
                )
                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<Wallet>>() {}.type
                        )
                }
                if (walletName.isEmpty()) {
                    var index = 1
                    walletETH.walletName = "ETH" + String.format("%02d", index)
                    if (saveWalletList.size > 0) {
                        saveWalletList.forEach {
                            if (it.walletName == walletETH.walletName) {
                                index++
                            }
                        }
                    }
                    walletETH.walletName = "ETH" + String.format("%02d", index)
                }
                saveWalletList.add(walletETH)
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
                putAddress(detailsList)
                var isValidEthSuccess = isEthValidAddress(walletETH.address)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletETH))
                if (isValidEthSuccess) {
                    startActivity(Intent(this, MainETHActivity::class.java))
                }
            }
            2 -> { //TRX
                val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim(),
                    mnemonicList
                )
                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<Wallet>>() {}.type
                        )
                }
                if (walletName.isEmpty()) {
                    var index = 1
                    walletTRX.walletName = "TRX" + String.format("%02d", index)
                    if (saveWalletList.size > 0) {
                        saveWalletList.forEach {
                            if (it.walletName == walletTRX.walletName) {
                                index++
                            }
                        }
                    }
                    walletTRX.walletName = "TRX" + String.format("%02d", index)
                }
                saveWalletList.add(walletTRX)
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
                putAddress(detailsList)
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletTRX))
                if (isValidTrxSuccess) {
                    startActivity(Intent(this, MainTRXActivity::class.java))
                }
            }
            3 -> { //BTC
                val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim(),
                    mnemonicList
                )
                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<Wallet>>() {}.type
                        )
                }
                if (walletName.isEmpty()) {
                    var index = 1
                    walletBTC.walletName = "BTC" + String.format("%02d", index)
                    if (saveWalletList.size > 0) {
                        saveWalletList.forEach {
                            if (it.walletName == walletBTC.walletName) {
                                index++
                            }
                        }
                    }
                    walletBTC.walletName = "BTC" + String.format("%02d", index)
                }
                saveWalletList.add(walletBTC)
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 3))
                putAddress(detailsList)
                var isValidBtcSuccess = isBtcValidAddress(walletBTC.address)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletBTC))
                if (isValidBtcSuccess) {
                    startActivity(Intent(this, MainBTCActivity::class.java))
                }
            }
            4 -> { //ALL
                var walletETH = WalletETHUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim(),
                    mnemonicList
                )

                val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim(),
                    mnemonicList
                )

                val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim(),
                    mnemonicList
                )

                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<Wallet>>() {}.type
                        )
                }
                if (walletName.isEmpty()) {
                    var index1 = 1
                    var index2 = 1
                    var index3 = 1
                    walletETH.walletName = "ETH" + String.format("%02d", index1)
                    walletTRX.walletName = "TRX" + String.format("%02d", index2)
                    walletBTC.walletName = "BTC" + String.format("%02d", index3)
                    if (saveWalletList.size > 0) {
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
                        }
                    }
                    walletETH.walletName = "ETH" + String.format("%02d", index1)
                    walletTRX.walletName = "TRX" + String.format("%02d", index2)
                    walletBTC.walletName = "BTC" + String.format("%02d", index3)
                }
                saveWalletList.add(walletETH)
                saveWalletList.add(walletTRX)
                saveWalletList.add(walletBTC)
                println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
                detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
                detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 3))
                putAddress(detailsList)
                //设置选择默认
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletETH))

                //2、之后地址校验
                var isValidEthSuccess = isEthValidAddress(walletETH.address)
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
                var isValidBtcSuccess = isBtcValidAddress(walletBTC.address)
                println("--->walletBTC.address:${walletBTC.address}")
                println("--->isValidBtcSuccess:${isValidBtcSuccess}")
                if (isValidEthSuccess && isValidTrxSuccess && isValidBtcSuccess) {
                    startActivity(Intent(this, MainETHActivity::class.java))
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun putAddress(detailsList: ArrayList<AddressReport.DetailsList>) {
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    putAddress(detailsList)
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }

}
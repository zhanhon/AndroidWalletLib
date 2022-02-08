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
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityContributingWordsBinding
import com.ramble.ramblewallet.ethereum.MnemonicUtils
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo


class ContributingWordsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityContributingWordsBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var contributingWordsAdapter: ContributingWordsAdapter
    private lateinit var mnemonicETH: ArrayList<String>
    private lateinit var mnemonicList: ArrayList<String>
    private lateinit var walletName: String
    private lateinit var walletPassword: String
    private var currentTab = "english"
    private var walletETHString: String = ""
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var walletType = 0 //链类型|0:BTC|1:ETH|2:TRX|3：BTC、ETH、TRX

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words)
        walletName = intent.getStringExtra(ARG_PARAM1)
        walletPassword = intent.getStringExtra(ARG_PARAM2)
        walletType = intent.getIntExtra(ARG_PARAM3, 0)

        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnOneCopy.setOnClickListener(this)
        binding.btnSkipThis.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        currentTab = "english"//默认英文
        binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
        binding.vChinese.setBackgroundResource(R.color.color_9598AA)
        // 生成钱包助记词
        when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            EN, CN -> {
                mnemonicList = MnemonicUtils.generateMnemonicEnglishChinese()
            }
            TW -> {
                mnemonicList = MnemonicUtils.generateMnemonicChineseTraditional()
            }
        }
        createContributingWordsPage(mnemonicList[0])
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_english -> {
                binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
                binding.vChinese.setBackgroundResource(R.color.color_9598AA)
                createContributingWordsPage(mnemonicList[0])
                currentTab = "english"
            }
            R.id.ll_chinese -> {
                binding.vEnglish.setBackgroundResource(R.color.color_9598AA)
                binding.vChinese.setBackgroundResource(R.color.color_3F5E94)
                createContributingWordsPage(mnemonicList[1])
                currentTab = "chinese"
            }
            R.id.btn_one_copy -> {
                when (currentTab) {
                    "english" -> {
                        ClipboardUtils.copy(mnemonicList[0])
                    }
                    "chiese" -> {
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
            startActivity(Intent(this, ContributingWordsConfirmActivity::class.java).apply {
                putStringArrayListExtra(ARG_PARAM1, mnemonicList)
                putExtra(ARG_PARAM2, walletName)
                putExtra(ARG_PARAM3, walletPassword)
                putExtra(ARG_PARAM4, currentTab)
                putExtra(ARG_PARAM5, walletType)
            })
        }
    }

    private fun skipConfirmHandle() {
        when (currentTab) {
            "english" -> {
                walletETHString = mnemonicList[0]
            }
            "chiese" -> {
                walletETHString = mnemonicList[1]
            }
        }

        when (walletType) {
            1 -> { //ETH
                var walletETH = WalletETHUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim()
                )
                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<WalletETH>>() {}.type
                        )
                }
                saveWalletList.add(walletETH)
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletETH.address, 1))
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
                    walletETHString.trim()
                )
                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<WalletETH>>() {}.type
                        )
                }
                saveWalletList.add(walletTRX)
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletTRX.address, 2))
                putAddress(detailsList)
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletTRX))
                if (isValidTrxSuccess) {
                    startActivity(Intent(this, MainTRXActivity::class.java))
                }
            }
            3 -> { //ALL
                var walletETH = WalletETHUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim()
                )

                val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim()
                )

                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<WalletETH>>() {}.type
                        )
                }
                saveWalletList.add(walletETH)
                saveWalletList.add(walletTRX)
                println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletETH.address, 1))
                detailsList.add(AddressReport.DetailsList(walletTRX.address, 2))
                putAddress(detailsList)
                //设置选择默认
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletETH))

                //2、之后地址校验
                var isValidEthSuccess = isEthValidAddress(walletETH.address)
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
                if (isValidEthSuccess && isValidTrxSuccess) {
                    startActivity(Intent(this, MainETHActivity::class.java))
                }
            }
            0 -> { //BTC

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
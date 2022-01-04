package com.ramble.ramblewallet.activity

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
import com.ramble.ramblewallet.eth.MnemonicUtils
import com.ramble.ramblewallet.eth.Wallet
import com.ramble.ramblewallet.eth.WalletManager
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
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
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words)
        walletName = intent.getStringExtra(ARG_PARAM1)
        walletPassword = intent.getStringExtra(ARG_PARAM2)
        initData()

        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnOneCopy.setOnClickListener(this)
        binding.btnSkipThis.setOnClickListener(this)
    }

    private fun initData() {
        if (SharedPreferencesUtils.getString(this, LANGUAGE, CN) == EN) {
            binding.llChinese.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
        binding.vChinese.setBackgroundResource(R.color.color_9598AA)
        // 生成钱包助记词
        when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                mnemonicList = MnemonicUtils.generateMnemonicChineseSimplified()
            }
            TW -> {
                mnemonicList = MnemonicUtils.generateMnemonicChineseTraditional()
            }
            EN -> {
                mnemonicList = MnemonicUtils.generateMnemonicEnglish()
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
                startActivity(Intent(this, MainETHActivity::class.java))
            }
        }
    }

    private fun createContributingWordsPage(mnemonicString: String) {
        myDataBeans.clear()
        mnemonicETH = mnemonicString.split(" ") as ArrayList<String>
        mnemonicETH.forEachIndexed { index, element ->
            myDataBeans.add(MyDataBean(index + 1, element, ""))
        }
        contributingWordsAdapter = ContributingWordsAdapter(myDataBeans)
        binding.rvContributingWords.adapter = contributingWordsAdapter
        binding.btnContributingWordsConfirm.setOnClickListener {
            startActivity(Intent(this, ContributingWordsConfirmActivity::class.java).apply {
                putStringArrayListExtra(ARG_PARAM1, mnemonicList)
                putExtra(ARG_PARAM2, walletName)
                putExtra(ARG_PARAM3, walletPassword)
                putExtra(ARG_PARAM4, currentTab)
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
            else -> {
                walletETHString = mnemonicList[0]
            }
        }
        //1、助记词生成keystore
        var walletETHKeyStore: Wallet =
            WalletManager.generateWalletKeystore(walletPassword, walletETHString.trim())
        walletETHKeyStore.walletName = walletName
        walletETHKeyStore.walletPassword = walletPassword
        println("-=-=-=->wallestETHAddress:${walletETHKeyStore.address}")
        println("-=-=-=->walletETHMnemonic:${walletETHKeyStore.mnemonic}")
        println("-=-=-=->walletETHPrivateKey:${walletETHKeyStore.privateKey}")
        println("-=-=-=->walletETHKeystore:${walletETHKeyStore.keystore}")

        putAddress(walletETHKeyStore)

        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
        }
        saveWalletList.add(walletETHKeyStore)
        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))


        //2、之后地址校验
        var isValidSuccess = WalletManager.isETHValidAddress(walletETHKeyStore.address)
        if (isValidSuccess) {
            println("-=-=-=->isValidSuccess:$isValidSuccess")
            startActivity(Intent(this, MainETHActivity::class.java))
        }
    }

    private fun putAddress(walletETHKeyStore: Wallet) {
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletETHKeyStore.address, 1)) //ETH
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    putAddress(walletETHKeyStore)
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }
}
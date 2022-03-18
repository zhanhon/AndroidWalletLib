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
import com.ramble.ramblewallet.adapter.ContributingWordsConfirmAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.custom.AutoLineFeedLayoutManager
import com.ramble.ramblewallet.databinding.ActivityContributingWordsConfirmBinding
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.ToastUtils
import com.ramble.ramblewallet.utils.applyIo


class MnemonicConfirmActivity : BaseActivity(), View.OnClickListener {

    private lateinit var contributingWordsConfirmAdapter: ContributingWordsConfirmAdapter
    private lateinit var binding: ActivityContributingWordsConfirmBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private var mnemonicETHShuffled: ArrayList<String> = arrayListOf()
    private var mnemonicETHOriginal: ArrayList<String> = arrayListOf()
    private var mnemonicETHChoose: ArrayList<String> = arrayListOf()
    private var walletETHString: String = ""
    private lateinit var walletName: String
    private lateinit var walletPassword: String
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var currentTab = ""
    private lateinit var mnemonicETH: List<String>
    private var walletType = 1 //链类型|1:ETH|2:TRX|3:BTC|4：BTC、ETH、TRX
    private var isBackupMnemonic = false
    private var mnemonic: String? = null
    private var times = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words_confirm)
        mnemonicETH = intent.getStringArrayListExtra(ARG_PARAM1)
        walletName = intent.getStringExtra(ARG_PARAM2)
        walletPassword = intent.getStringExtra(ARG_PARAM3)
        currentTab = intent.getStringExtra(ARG_PARAM4)
        walletType = intent.getIntExtra(ARG_PARAM5, 1)
        isBackupMnemonic = intent.getBooleanExtra(ARG_PARAM6, false)
        mnemonic = intent.getStringExtra(ARG_PARAM7)

        initData()
        mnmonicETHClick()

        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnContributingWordsCompleted.setOnClickListener {
            if (mnemonicETHChoose == mnemonicETHOriginal) {
                if (mnemonic != null) {
                    SharedPreferencesUtils.saveBoolean(this, IS_CONFIRM_MNEMONIC, true)
                    MnemonicActivity.instance.finish()
                    finish()
                } else {
                    createWallet()
                }
            } else {
                ToastUtils.showToastFree(this, getString(R.string.confirm_contributing_words_error))
            }
        }
    }

    private fun createWallet() {
        mnemonicETHOriginal.forEach {
            walletETHString = "$walletETHString$it "
        }
        when (walletType) {
            1 -> { //ETH
                var walletETH = WalletETHUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    mnemonicETH[0],
                    mnemonicETH
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
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETINFO,
                    Gson().toJson(saveWalletList)
                )
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
                var isValidEthSuccess = isEthValidAddress(walletETH.address)
                if (isValidEthSuccess) {
                    putAddress(detailsList)
                    SharedPreferencesUtils.saveString(
                        this,
                        WALLETSELECTED,
                        Gson().toJson(walletETH)
                    )
                    startActivity(Intent(this, MainETHActivity::class.java))
                }
            }
            2 -> { //TRON
                val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    mnemonicETH[0],
                    mnemonicETH
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
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETINFO,
                    Gson().toJson(saveWalletList)
                )
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
                if (isValidTrxSuccess) {
                    putAddress(detailsList)
                    SharedPreferencesUtils.saveString(
                        this,
                        WALLETSELECTED,
                        Gson().toJson(walletTRX)
                    )
                    startActivity(Intent(this, MainTRXActivity::class.java))
                }
            }
            3 -> { //BTC
                val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    mnemonicETH[0],
                    mnemonicETH
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
                var isValidBtcSuccess = WalletBTCUtils.isBtcValidAddress(walletBTC.address)
                if (isValidBtcSuccess) {
                    putAddress(detailsList)
                    SharedPreferencesUtils.saveString(
                        this,
                        WALLETSELECTED,
                        Gson().toJson(walletBTC)
                    )
                    startActivity(Intent(this, MainBTCActivity::class.java))
                }
            }
            4 -> { //ALL
                var walletETH = WalletETHUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    mnemonicETH[0],
                    mnemonicETH
                )

                val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    mnemonicETH[0],
                    mnemonicETH
                )

                val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    mnemonicETH[0],
                    mnemonicETH
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

                //2、之后地址校验
                var isValidEthSuccess = isEthValidAddress(walletETH.address)
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
                var isValidBtcSuccess = WalletBTCUtils.isBtcValidAddress(walletBTC.address)
                if (isValidEthSuccess && isValidTrxSuccess && isValidBtcSuccess) {
                    putAddress(detailsList)
                    //设置选择默认
                    SharedPreferencesUtils.saveString(
                        this,
                        WALLETSELECTED,
                        Gson().toJson(walletETH)
                    )
                    startActivity(Intent(this, MainETHActivity::class.java))
                }
            }
        }
    }

    private fun initData() {
        when (currentTab) {
            "english" -> {
                binding.vChinese.visibility = View.INVISIBLE
                binding.vEnglish.visibility = View.VISIBLE
                binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
                mnemonicETHOriginal = mnemonicETH[0].split(" ") as ArrayList<String>
                mnemonicETHShuffled = mnemonicETH[0].split(" ") as ArrayList<String>
                mnemonicETHShuffled.shuffle()
            }
            "chinese" -> {
                binding.vEnglish.visibility = View.INVISIBLE
                binding.vChinese.visibility = View.VISIBLE
                binding.vChinese.setBackgroundResource(R.color.color_3F5E94)
                mnemonicETHOriginal = mnemonicETH[1].split(" ") as ArrayList<String>
                mnemonicETHShuffled = mnemonicETH[1].split(" ") as ArrayList<String>
                mnemonicETHShuffled.shuffle()
            }
        }
        initMnmonicETH(mnemonicETHShuffled)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_english -> {
                currentTab = "english"
                initData()
                initSwitch()
            }
            R.id.ll_chinese -> {
                currentTab = "chinese"
                initData()
                initSwitch()
            }
        }
    }

    private fun initSwitch() {
        myDataBeans.clear()
        mnemonicETHChoose.clear()
        binding.tvContributingWordsConfirmTips.visibility = View.VISIBLE
        binding.tvContributingWordsName1.visibility = View.VISIBLE
        binding.tvContributingWordsName2.visibility = View.VISIBLE
        binding.tvContributingWordsName3.visibility = View.VISIBLE
        binding.tvContributingWordsName4.visibility = View.VISIBLE
        binding.tvContributingWordsName5.visibility = View.VISIBLE
        binding.tvContributingWordsName6.visibility = View.VISIBLE
        binding.tvContributingWordsName7.visibility = View.VISIBLE
        binding.tvContributingWordsName8.visibility = View.VISIBLE
        binding.tvContributingWordsName9.visibility = View.VISIBLE
        binding.tvContributingWordsName10.visibility = View.VISIBLE
        binding.tvContributingWordsName11.visibility = View.VISIBLE
        binding.tvContributingWordsName12.visibility = View.VISIBLE
    }

    @SuppressLint("CheckResult")
    private fun putAddress(detailsList: ArrayList<AddressReport.DetailsList>) {
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    if (times < 3) {
                        putAddress(detailsList)
                        times++
                    }
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }

    private fun initMnmonicETH(mnemonicETHShuffled: ArrayList<String>) {
        binding.tvContributingWordsName1.text = mnemonicETHShuffled[0]
        binding.tvContributingWordsName2.text = mnemonicETHShuffled[1]
        binding.tvContributingWordsName3.text = mnemonicETHShuffled[2]
        binding.tvContributingWordsName4.text = mnemonicETHShuffled[3]
        binding.tvContributingWordsName5.text = mnemonicETHShuffled[4]
        binding.tvContributingWordsName6.text = mnemonicETHShuffled[5]
        binding.tvContributingWordsName7.text = mnemonicETHShuffled[6]
        binding.tvContributingWordsName8.text = mnemonicETHShuffled[7]
        binding.tvContributingWordsName9.text = mnemonicETHShuffled[8]
        binding.tvContributingWordsName10.text = mnemonicETHShuffled[9]
        binding.tvContributingWordsName11.text = mnemonicETHShuffled[10]
        binding.tvContributingWordsName12.text = mnemonicETHShuffled[11]
    }

    private fun contributingWordsFailDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_contributing_words_fail)
            dialogCenterTheme(window)

            if (isBackupMnemonic) {
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
                if (isBackupMnemonic) {
                    SharedPreferencesUtils.saveBoolean(this, IS_CONFIRM_MNEMONIC, false)
                    MnemonicActivity.instance.finish()
                    finish()
                } else {
                    when (walletType) {
                        1 -> {
                            startActivity(Intent(this, MainETHActivity::class.java))
                        }
                        2 -> {
                            startActivity(Intent(this, MainTRXActivity::class.java))
                        }
                        3 -> {
                            startActivity(Intent(this, MainBTCActivity::class.java))
                        }
                        4 -> {
                            startActivity(Intent(this, CreateRecoverWalletActivity::class.java))
                        }
                    }
                }
                dialog.dismiss()
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

    private var clickErrorCount = 0
    private fun mnmonicETHClick() {
        binding.tvContributingWordsName1.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName1.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE
            myDataBeans.add(MyDataBean(0, binding.tvContributingWordsName1.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName1.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }

            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName2.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName2.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(1, binding.tvContributingWordsName2.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName2.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName3.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName3.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(2, binding.tvContributingWordsName3.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName3.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName4.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName4.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(3, binding.tvContributingWordsName4.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName4.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName5.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName5.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(4, binding.tvContributingWordsName5.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName5.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName6.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName6.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(5, binding.tvContributingWordsName6.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName6.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName7.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName7.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(6, binding.tvContributingWordsName7.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName7.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName8.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName8.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(7, binding.tvContributingWordsName8.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName8.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName9.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName9.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(8, binding.tvContributingWordsName9.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName9.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName10.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName10.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(9, binding.tvContributingWordsName10.text.toString(), false))
            mnemonicETHChoose.add(binding.tvContributingWordsName10.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName11.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName11.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(
                MyDataBean(
                    10,
                    binding.tvContributingWordsName11.text.toString(),
                    false
                )
            )
            mnemonicETHChoose.add(binding.tvContributingWordsName11.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName12.setOnClickListener {
            myDataBeans.forEach {
                if (it.isWrong) {
                    return@setOnClickListener
                }
            }
            binding.tvContributingWordsName12.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(
                MyDataBean(
                    11,
                    binding.tvContributingWordsName12.text.toString(),
                    false
                )
            )
            mnemonicETHChoose.add(binding.tvContributingWordsName12.text.toString())
            mnemonicETHChoose.forEachIndexed { index, element ->
                if (element != mnemonicETHOriginal[index]) {
                    if (clickErrorCount >= 3) {
                        contributingWordsFailDialog()
                    }
                    myDataBeans[index].isWrong = true
                    clickErrorCount++
                }
            }
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
    }

    private fun contributingWordsConfirmClick() {
        if (myDataBeans.size == 12) {
            binding.btnContributingWordsCompleted.isEnabled = true
            binding.btnContributingWordsCompleted.background =
                getDrawable(R.drawable.shape_green_bottom_btn)
        } else {
            binding.btnContributingWordsCompleted.isEnabled = false
            binding.btnContributingWordsCompleted.background =
                getDrawable(R.drawable.shape_gray_bottom_btn)
        }
        contributingWordsConfirmAdapter.setOnItemClickListener { _, _, position ->
            when (position) {
                0 -> {
                    setItemClickSub(position)
                }
                1 -> {
                    setItemClickSub(position)
                }
                2 -> {
                    setItemClickSub(position)
                }
                3 -> {
                    setItemClickSub(position)
                }
                4 -> {
                    setItemClickSub(position)
                }
                5 -> {
                    setItemClickSub(position)
                }
                6 -> {
                    setItemClickSub(position)
                }
                7 -> {
                    setItemClickSub(position)
                }
                8 -> {
                    setItemClickSub(position)
                }
                9 -> {
                    setItemClickSub(position)
                }
                10 -> {
                    setItemClickSub(position)
                }
                11 -> {
                    setItemClickSub(position)
                }
            }
        }
    }

    private fun setItemClickSub(position: Int) {
        position(myDataBeans[position].index)
        mnemonicETHChoose.remove(mnemonicETHChoose[position])
        contributingWordsConfirmAdapter.remove(myDataBeans[position])
        contributingWordsConfirmAdapter.notifyDataSetChanged()
    }

    private fun position(position: Int) {
        when (position) {
            0 -> {
                binding.tvContributingWordsName1.visibility = View.VISIBLE
            }
            1 -> {
                binding.tvContributingWordsName2.visibility = View.VISIBLE
            }
            2 -> {
                binding.tvContributingWordsName3.visibility = View.VISIBLE
            }
            3 -> {
                binding.tvContributingWordsName4.visibility = View.VISIBLE
            }
            4 -> {
                binding.tvContributingWordsName5.visibility = View.VISIBLE
            }
            5 -> {
                binding.tvContributingWordsName6.visibility = View.VISIBLE
            }
            6 -> {
                binding.tvContributingWordsName7.visibility = View.VISIBLE
            }
            7 -> {
                binding.tvContributingWordsName8.visibility = View.VISIBLE
            }
            8 -> {
                binding.tvContributingWordsName9.visibility = View.VISIBLE
            }
            9 -> {
                binding.tvContributingWordsName10.visibility = View.VISIBLE
            }
            10 -> {
                binding.tvContributingWordsName11.visibility = View.VISIBLE
            }
            11 -> {
                binding.tvContributingWordsName12.visibility = View.VISIBLE
            }
        }
    }
}
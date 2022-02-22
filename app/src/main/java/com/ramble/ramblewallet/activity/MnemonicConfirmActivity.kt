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
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.ContributingWordsConfirmAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.custom.AutoLineFeedLayoutManager
import com.ramble.ramblewallet.databinding.ActivityContributingWordsConfirmBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.utils.toastDefault


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
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var currentTab = ""
    private lateinit var mnemonicETH: ArrayList<String>
    private var walletType = 0 //链类型|0:BTC|1:ETH|2:TRX|3：BTC、ETH、TRX
    private var isBackupMnemonic = false
    private var mnemonic: String? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words_confirm)
        mnemonicETH = intent.getStringArrayListExtra(ARG_PARAM1)
        walletName = intent.getStringExtra(ARG_PARAM2)
        walletPassword = intent.getStringExtra(ARG_PARAM3)
        currentTab = intent.getStringExtra(ARG_PARAM4)
        walletType = intent.getIntExtra(ARG_PARAM5, 0)
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
                toastDefault(getString(R.string.confirm_contributing_words_error))
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
                    walletETHString.trim()
                )
                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<WalletETH>>() {}.type
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
                putAddress(detailsList)
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(walletETH)
                )
                var isValidEthSuccess = isEthValidAddress(walletETH.address)
                if (isValidEthSuccess) {
                    startActivity(Intent(this, MainETHActivity::class.java))
                }
            }
            2 -> { //TRON
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
                putAddress(detailsList)
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(walletTRX)
                )
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
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
                if (walletName.isEmpty()) {
                    var index1 = 1
                    var index2 = 1
                    walletETH.walletName = "ETH" + String.format("%02d", index1)
                    walletTRX.walletName = "TRX" + String.format("%02d", index2)
                    if (saveWalletList.size > 0) {
                        saveWalletList.forEach {
                            if (it.walletName == walletETH.walletName) {
                                index1++
                            }
                            if (it.walletName == walletTRX.walletName) {
                                index2++
                            }
                        }
                    }
                    walletETH.walletName = "ETH" + String.format("%02d", index1)
                    walletTRX.walletName = "TRX" + String.format("%02d", index2)
                }
                saveWalletList.add(walletETH)
                saveWalletList.add(walletTRX)
                println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETINFO,
                    Gson().toJson(saveWalletList)
                )
                var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
                detailsList.add(AddressReport.DetailsList(walletETH.address, 0, 1))
                detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
                putAddress(detailsList)
                //设置选择默认
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(walletETH)
                )

                //2、之后地址校验
                var isValidEthSuccess = isEthValidAddress(walletETH.address)
                var isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
                if (isValidEthSuccess && isValidTrxSuccess) {
                    startActivity(Intent(this, MainETHActivity::class.java))
                }
            }
            0 -> { //BTC
                val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
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
                detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 0))
                putAddress(detailsList)
                var isValidBtcSuccess = WalletBTCUtils.isBtcValidAddress(walletBTC.address, true)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletBTC))
                if (isValidBtcSuccess) {
                    startActivity(Intent(this, MainBTCActivity::class.java))
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
                window.findViewById<Button>(R.id.btn_cancel_create).setText(R.string.cancel)
                window.findViewById<Button>(R.id.btn_skip).visibility = View.GONE
            } else {
                window.findViewById<Button>(R.id.btn_cancel_create).setText(R.string.cancel_create)
                window.findViewById<Button>(R.id.btn_skip).visibility = View.VISIBLE
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
                            startActivity(Intent(this, CreateRecoverWalletActivity::class.java))
                        }
                        0 -> {
                            startActivity(Intent(this, MainBTCActivity::class.java))
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            if (clickErrorCount >= 3) {
                contributingWordsFailDialog()
            }
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
            binding.btnContributingWordsCompleted.background =
                getDrawable(R.drawable.shape_green_bottom_btn)
        } else {
            binding.btnContributingWordsCompleted.background =
                getDrawable(R.drawable.shape_gray_bottom_btn)
        }
        contributingWordsConfirmAdapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                1 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                2 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                3 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                4 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                5 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                6 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                7 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                8 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                9 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                10 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                11 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
            }
        }
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
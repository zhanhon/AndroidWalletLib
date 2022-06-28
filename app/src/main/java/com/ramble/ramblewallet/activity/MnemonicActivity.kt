package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.view.View
import android.view.WindowManager
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
import com.ramble.ramblewallet.custom.RecyclerGridDecoration
import com.ramble.ramblewallet.databinding.ActivityMnemonicBinding
import com.ramble.ramblewallet.fragment.MainBTCFragment
import com.ramble.ramblewallet.fragment.MainETHFragment
import com.ramble.ramblewallet.fragment.MainSOLFragment
import com.ramble.ramblewallet.fragment.MainTRXFragment
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*

class MnemonicActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMnemonicBinding
    private lateinit var contributingWordsAdapter: MnemonicAdapter
    private lateinit var mnemonicETH: ArrayList<String>
    private lateinit var mnemonicList: List<String>
    private lateinit var currentTab: String
    private lateinit var walletSelleted: Wallet
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private var walletETHString: String = ""
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var putAddressTimes = 0
    private var isAlreadyBackupMnemonic = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mnemonic)
        walletSelleted = intent.getSerializableExtra(ARG_PARAM1) as Wallet
        initClick()
    }

    override fun onResume() {
        super.onResume()
        //字符串缩进
        val spannableString = SpannableString(getString(R.string.mnemonic_hint_operate))
        val what = LeadingMarginSpan.Standard(0, DisplayHelper.dp2px(this,15))
        spannableString.setSpan(what, 0, spannableString.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        binding.tvHintOperate.text = spannableString

        currentTab = "english"//默认英文
        binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
        binding.vChinese.visibility = View.INVISIBLE
        if ((walletSelleted.mnemonic != null) && (walletSelleted.mnemonicList != null)) {
            mnemonicList = walletSelleted.mnemonicList as ArrayList<String>
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
        if (walletSelleted.address.isNotEmpty()) {
            binding.btnOneCopy.visibility = View.GONE
            binding.btnSkipThis.visibility = View.GONE
        } else {
            binding.btnOneCopy.visibility = View.VISIBLE
            binding.btnSkipThis.visibility = View.VISIBLE
        }
        createContributingWordsPage(mnemonicList[0])
        val walletList = SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "")
        if (walletList.isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(walletList, object : TypeToken<ArrayList<Wallet>>() {}.type)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
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
                if (walletSelleted.address.isNotEmpty()) {
                    val walletTemp = Wallet(
                        walletSelleted.walletName,
                        walletSelleted.walletPassword,
                        walletSelleted.mnemonic,
                        walletSelleted.address,
                        walletSelleted.privateKey,
                        walletSelleted.keystore,
                        walletSelleted.walletType,
                        walletSelleted.mnemonicList
                    )
                    walletTemp.index = walletSelleted.index
                    walletTemp.isBackupAlready = true
                    updateListData(walletTemp)
                    SharedPreferencesUtils.saveSecurityString(
                        this,
                        WALLETSELECTED,
                        Gson().toJson(walletTemp)
                    )
                    SharedPreferencesUtils.saveSecurityString(
                        this,
                        WALLETINFO,
                        Gson().toJson(saveWalletList)
                    )
                }
                isAlreadyBackupMnemonic = true
            }
            R.id.btn_skip_this -> {
                if (isAlreadyBackupMnemonic) {
                    skipConfirmHandle()
                } else {
                    showCommonDialog(this,
                        title = getString(R.string.tips),
                        titleContent = getString(R.string.mnemonic_no_backup_tips),
                        btnCancel = getString(R.string.cancel),
                        btnConfirm = getString(R.string.btn_confirm),
                        confirmListener = {
                            skipConfirmHandle()
                        }
                    )
                }
            }
        }
    }

    private fun updateListData(walletTemp: Wallet) {
        var position = 0
        saveWalletList.forEachIndexed { index, wallet ->
            if (wallet.address == walletSelleted.address) {
                position = index
            }
        }
        saveWalletList.removeAt(position)
        saveWalletList.add(position, walletTemp)
    }

    private fun initClick() {
        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnOneCopy.setOnClickListener(this)
        binding.btnSkipThis.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
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
        binding.rvContributingWords.addItemDecoration(RecyclerGridDecoration(this,3))

//        val dividerVertical = DividerItemDecoration(this, RecyclerView.VERTICAL)
//        dividerVertical.setDrawable(resources.getDrawable(R.drawable.shape_item_decoration))
//        binding.rvContributingWords.addItemDecoration(dividerVertical)
//        val dividerHorizontal = DividerItemDecoration(this, RecyclerView.HORIZONTAL)
//        dividerHorizontal.setDrawable(resources.getDrawable(R.drawable.shape_item_decoration))
//        binding.rvContributingWords.addItemDecoration(dividerHorizontal)

        binding.btnContributingWordsConfirm.setOnClickListener {
            startActivity(Intent(this, MnemonicConfirmActivity::class.java).apply {
                putExtra(ARG_PARAM1, walletSelleted)
                putStringArrayListExtra(ARG_PARAM2, ArrayList(mnemonicList))
                putExtra(ARG_PARAM3, currentTab)
            })
        }
    }

    private fun skipConfirmHandle() {
        showLoadingDialog()
        if (DoubleUtils.isFastDoubleClick()) return
        when (currentTab) {
            "english" -> {
                walletETHString = mnemonicList[0]
            }
            "chinese" -> {
                walletETHString = mnemonicList[1]
            }
        }
        when (walletSelleted.walletType) {
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
//        finish()
    }

    private fun allLogicHandle() {
        val walletETH = WalletETHUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        val walletBTC = WalletBTCUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicList[0],
            mnemonicList
        )
        val walletSOL = WalletSOLUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicList[0],
            mnemonicList
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
            if (isAlreadyBackupMnemonic) {
                walletSOL.isBackupAlready = true
                walletTRX.isBackupAlready = true
                walletBTC.isBackupAlready = true
                walletETH.isBackupAlready = true
            }
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
            mnemonicList[0],
            mnemonicList
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
            if (isAlreadyBackupMnemonic) walletSOL.isBackupAlready = true
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
            mnemonicList[0],
            mnemonicList
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
            if (isAlreadyBackupMnemonic) walletBTC.isBackupAlready = true
            saveWalletList.add(walletBTC)
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            detailsList.add(AddressReport.DetailsList(walletBTC.address, 0, 3))
            val isValidBtcSuccess = isBtcValidAddress(walletBTC.address)
            if (isValidBtcSuccess) {
                putAddress(detailsList)
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletBTC))
                RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE, MainBTCFragment::class.toString())
            }
        }
    }

    private fun trxLogicHandle() {
        val walletTRX = WalletTRXUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicList[0],
            mnemonicList
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
            if (isAlreadyBackupMnemonic) walletTRX.isBackupAlready = true
            saveWalletList.add(walletTRX)
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            detailsList.add(AddressReport.DetailsList(walletTRX.address, 0, 2))
            val isValidTrxSuccess = isTrxValidAddress(walletTRX.address)
            if (isValidTrxSuccess) {
                putAddress(detailsList)
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletTRX))
                RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainTRXFragment::class.toString())
            }
        }
    }

    private fun ethLogicHandle() {
        val walletETH = WalletETHUtils.generateWalletByMnemonic(
            walletSelleted.walletName,
            walletSelleted.walletPassword,
            mnemonicList[0],
            mnemonicList
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
            if (isAlreadyBackupMnemonic) walletETH.isBackupAlready = true
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
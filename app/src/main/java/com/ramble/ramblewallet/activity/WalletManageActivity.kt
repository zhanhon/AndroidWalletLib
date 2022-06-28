package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.IdWalletManageAdapter
import com.ramble.ramblewallet.adapter.WalletManageAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.AllTokenBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityWalletManageBinding
import com.ramble.ramblewallet.fragment.MainBTCFragment
import com.ramble.ramblewallet.fragment.MainETHFragment
import com.ramble.ramblewallet.fragment.MainSOLFragment
import com.ramble.ramblewallet.fragment.MainTRXFragment
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

class WalletManageActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener,
    View.OnClickListener {

    private lateinit var binding: ActivityWalletManageBinding
    private var walletManageBean: ArrayList<Wallet> = arrayListOf()
    private var idWalletManageBean: ArrayList<Wallet> = arrayListOf()//身份钱包
    private var importWalletManageBean: ArrayList<Wallet> = arrayListOf()//创建导入钱包
    private var walletManageCurrencyBean: ArrayList<Wallet> = arrayListOf()
    private lateinit var walletManageAdapter: WalletManageAdapter
    private lateinit var idWalletAdapter: IdWalletManageAdapter
    private var isDeletePage = false
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var saveWalletListSorted: ArrayList<Wallet> = arrayListOf()
    private lateinit var walletSelleted: Wallet
    private var putAddressTimes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manage)
        initView()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )
        val list = saveWalletList.iterator()
        list.forEach {
            if (it.address.isEmpty()) {
                list.remove()
            }
        }
        saveWalletListSorted = ArrayList(saveWalletList.sortedByDescending { it.index })
        SharedPreferencesUtils.saveSecurityString(
            this,
            WALLETINFO,
            Gson().toJson(saveWalletListSorted)
        )
        if (saveWalletListSorted.isNotEmpty()) {
            walletManageBean = saveWalletListSorted
            loadData(walletManageBean)
        }
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        binding.groupButton.check(R.id.check_all)

        walletManageAdapter = WalletManageAdapter()
        binding.rvMainCurrency.adapter = walletManageAdapter
        walletManageAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItem(position) is Wallet) {
                SharedPreferencesUtils.saveSecurityString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(adapter.getItem(position) as Wallet)
                )
                when ((adapter.getItem(position) as Wallet).walletType) {
                    WALLET_TYPE_ETH -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainETHFragment::class.toString())
                    }
                    WALLET_TYPE_TRX -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainTRXFragment::class.toString())
                    }
                    WALLET_TYPE_BTC -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainBTCFragment::class.toString())
                    }
                    WALLET_TYPE_SOL -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainSOLFragment::class.toString())
                    }
                }
            }
        }
        walletManageAdapter.setOnItemChildClickListener { adapter, view, position ->
            walletManageItemClick(view, adapter, position, importWalletManageBean)
        }

        //身份钱包
        idWalletAdapter = IdWalletManageAdapter()
        binding.rvIdWallet.adapter = idWalletAdapter
        idWalletAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItem(position) is Wallet) {
                SharedPreferencesUtils.saveSecurityString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(adapter.getItem(position) as Wallet)
                )
                when ((adapter.getItem(position) as Wallet).walletType) {
                    WALLET_TYPE_ETH -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainETHFragment::class.toString())
                    }
                    WALLET_TYPE_TRX -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainTRXFragment::class.toString())
                    }
                    WALLET_TYPE_BTC -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainBTCFragment::class.toString())
                    }
                    WALLET_TYPE_SOL -> {
                        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainSOLFragment::class.toString())
                    }
                }
            }
        }
        idWalletAdapter.setOnItemChildClickListener { adapter, view, position ->
            walletManageItemClick(view, adapter, position, idWalletManageBean)
        }
    }

    private fun initListener() {
        binding.groupButton.setOnCheckedChangeListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivManageWalletRight.setOnClickListener(this)
        binding.ivAddWallet.setOnClickListener(this)
        binding.tvDefaultWallet.setOnClickListener(this)
        binding.ivResetPassword.setOnClickListener(this)
        binding.ivMine.setOnClickListener(this)
    }

    private fun loadData(walletManageBean: ArrayList<Wallet>) {
        idWalletManageBean.clear()
        importWalletManageBean.clear()
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        walletManageBean.forEach {
            it.isChoose = it.address == walletSelleted.address
            if (it.isIdWallet){
                idWalletManageBean.add(it)
            }else{
                importWalletManageBean.add(it)
            }
        }
        walletManageAdapter.setNeedDelete(isDeletePage)
        walletManageAdapter.setList(importWalletManageBean)

        idWalletAdapter.setList(idWalletManageBean)

    }

    private fun walletManageItemClick(
        view: View,
        adapter: BaseQuickAdapter<*, *>,
        position: Int,
        walletManageBean: ArrayList<Wallet>
    ) {
        when (view.id) {
            R.id.iv_copy_address -> {
                if (adapter.getItem(position) is Wallet) {
                    ClipboardUtils.copy((adapter.getItem(position) as Wallet).address, this)
                }
            }
            R.id.iv_wallet_more -> {
                startActivity(Intent(this, WalletMoreOperateActivity::class.java).apply {
                    putExtra(ARG_PARAM1, Gson().toJson(adapter.getItem(position) as Wallet))
                })
            }
            R.id.cl_delete -> {
                if (adapter.getItem(position) is Wallet) {
                    if ((adapter.getItem(position) as Wallet).isClickDelete) {
                        walletManageBean[position] =
                            Wallet(
                                (adapter.getItem(position) as Wallet).walletName,
                                (adapter.getItem(position) as Wallet).walletPassword,
                                (adapter.getItem(position) as Wallet).mnemonic,
                                (adapter.getItem(position) as Wallet).address,
                                (adapter.getItem(position) as Wallet).privateKey,
                                (adapter.getItem(position) as Wallet).keystore,
                                (adapter.getItem(position) as Wallet).walletType,
                                (adapter.getItem(position) as Wallet).mnemonicList,
                                false,
                            )
                    } else {
                        walletManageBean[position] =
                            Wallet(
                                (adapter.getItem(position) as Wallet).walletName,
                                (adapter.getItem(position) as Wallet).walletPassword,
                                (adapter.getItem(position) as Wallet).mnemonic,
                                (adapter.getItem(position) as Wallet).address,
                                (adapter.getItem(position) as Wallet).privateKey,
                                (adapter.getItem(position) as Wallet).keystore,
                                (adapter.getItem(position) as Wallet).walletType,
                                (adapter.getItem(position) as Wallet).mnemonicList,
                                true
                            )
                    }
                    walletManageAdapter.setData(position,walletManageBean[position])
                }
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.check_all -> {
                binding.tvDefaultWallet.visibility = View.GONE
                loadData(walletManageBean)
            }
            R.id.check_eth -> {
                switchRadio(WALLET_TYPE_ETH)
            }
            R.id.check_btc -> {
                switchRadio(WALLET_TYPE_BTC)
            }
            R.id.check_trx -> {
                switchRadio(WALLET_TYPE_TRX)
            }
            R.id.check_sol -> {
                switchRadio(WALLET_TYPE_SOL)
            }
        }
    }

    private fun switchRadio(walletType: Int) {
        walletManageCurrencyBean.clear()
        walletManageBean.forEach {
            if (it.walletType == walletType) {
                walletManageCurrencyBean.add(it)
            }
        }
        if (walletManageCurrencyBean.size == 0) {
            binding.tvDefaultWallet.visibility = View.VISIBLE
        } else {
            binding.tvDefaultWallet.visibility = View.GONE
            loadData(walletManageCurrencyBean)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_mine -> {
                startActivity(Intent(this, MineActivity::class.java))
            }
            R.id.iv_reset_password -> {
                startActivity(Intent(this, ResetPasswordActivity::class.java))
            }
            R.id.iv_add_wallet -> {
                startActivity(Intent(this, CreateWalletListActivity::class.java))
            }
            R.id.iv_manage_wallet_right -> {
                manageWalletRightSub()
            }
            R.id.tv_default_wallet -> {
                startActivity(Intent(this, CreateWalletListActivity::class.java))
            }
        }
    }

    private fun manageWalletRightSub() {
        if (importWalletManageBean.size == 0){
            ToastUtils.showToastFree(this, getString(R.string.no_wallet_to_delete))
            return
        }
        if (isDeletePage) {
            var isAllClickDelete = true
            walletManageBean.iterator().forEach {
                isAllClickDelete = isAllClickDelete && it.isClickDelete
            }
            if (isAllClickDelete) {
                ToastUtils.showToastFree(this, getString(R.string.least_save_wallet))
                isDeletePage = false
                binding.ivManageWalletRight.setImageResource(R.mipmap.qb_ic_delete)
                loadData(walletManageBean)
            } else {
                deleteConfirmTipsDialog()
            }
        } else {
            binding.checkAll.performClick()
            isDeletePage = true
            binding.ivManageWalletRight.setImageResource(R.drawable.ic_delelet_line)
            loadData(walletManageBean)
        }
    }


    private fun deleteConfirmTipsDialog() {
        showCommonDialog(this,
            title = getString(R.string.tips),
            titleContent = getString(R.string.please_confirm_delete_wallet),
            btnCancel = getString(R.string.cancel),
            btnConfirm = getString(R.string.btn_confirm),
            confirmListener = { btnConfirmSub() },
            btnCancleListener = {
                isDeletePage = false
                binding.ivManageWalletRight.setImageResource(R.mipmap.qb_ic_delete)
                loadData(walletManageBean)
            }
        )
    }

    private fun btnConfirmSub() {
        val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        val delete: ArrayList<Wallet> = arrayListOf()
        walletManageBean.forEach {
            importWalletManageBean.forEach { impotWallet ->
                if (impotWallet.isClickDelete && impotWallet.address == it.address) {
                    it.isClickDelete = true
                    detailsList.add(AddressReport.DetailsList(it.address, 2, it.walletType))
                    delete.add(it)
                } else {
                    detailsList.add(AddressReport.DetailsList(it.address, 0, it.walletType))
                }
            }

        }
        putAddress(detailsList)
        val list = walletManageBean.iterator()
        list.forEach {
            if (it.isClickDelete) {
                list.remove()
                if (it.isChoose) {
                    walletManageBean[0].isChoose = true
                }
            }
        }
        SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletManageBean[0]))
        saveWalletList = walletManageBean
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
        isDeletePage = false
        binding.ivManageWalletRight.setImageResource(R.mipmap.qb_ic_delete)
        loadData(walletManageBean)
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
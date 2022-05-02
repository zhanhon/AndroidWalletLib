package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.WalletManageAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.AllTokenBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityWalletManageBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader


class WalletManageActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener,
    View.OnClickListener {

    private lateinit var binding: ActivityWalletManageBinding
    private var walletManageBean: ArrayList<Wallet> = arrayListOf()
    private var walletManageCurrencyBean: ArrayList<Wallet> = arrayListOf()
    private lateinit var walletManageAdapter: WalletManageAdapter
    private var isDeletePage = false
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var saveWalletListSorted: ArrayList<Wallet> = arrayListOf()
    private lateinit var walletSelleted: Wallet
    private var putAddressTimes = 0
    private var myAllToken: ArrayList<AllTokenBean> = arrayListOf()
    private var isFromMine = false
    private var isWalletCurrency = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manage)
        isFromMine = intent.getBooleanExtra(ARG_PARAM1, false)
        binding.lyPullRefresh.setRefreshHeader(ClassicsHeader(this))
        binding.lyPullRefresh.setRefreshFooter(ClassicsFooter(this))
        //刷新的监听事件
        binding.lyPullRefresh.setOnRefreshListener {
            binding.lyPullRefresh.finishRefresh() //刷新完成
        }
        //加载的监听事件
        binding.lyPullRefresh.setOnLoadMoreListener {
            binding.lyPullRefresh.finishLoadMore() //加载完成
        }
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
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletListSorted))
        initView()
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        if (saveWalletListSorted.isNotEmpty()) {
            walletManageBean = saveWalletListSorted
            loadData(walletManageBean)
        }
        binding.groupButton.check(R.id.check_all)
    }

    private fun initListener() {
        binding.groupButton.setOnCheckedChangeListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivManageWalletRight.setOnClickListener(this)
        binding.ivAddWallet.setOnClickListener(this)
        binding.tvDefaultWallet.setOnClickListener(this)
    }

    private fun loadData(walletManageBean: ArrayList<Wallet>) {
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        walletManageBean.forEach {
            it.isChoose = it.address == walletSelleted.address
        }
        walletManageAdapter = WalletManageAdapter(walletManageBean, isDeletePage)
        binding.rvMainCurrency.adapter = walletManageAdapter
        walletManageAdapter.setOnItemClickListener { adapter, view, position ->
            if (adapter.getItem(position) is Wallet) {
                SharedPreferencesUtils.saveSecurityString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(adapter.getItem(position) as Wallet)
                )
                when ((adapter.getItem(position) as Wallet).walletType) {
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
                        startActivity(Intent(this, MainSOLActivity::class.java))
                    }
                }
            }
        }
        walletManageAdapter.addChildClickViewIds(R.id.iv_copy_address)
        walletManageAdapter.addChildClickViewIds(R.id.iv_wallet_more)
        walletManageAdapter.addChildClickViewIds(R.id.cl_delete)
        walletManageAdapter.setOnItemChildClickListener { adapter, view, position ->
            walletManageItemClick(view, adapter, position, walletManageBean)
        }
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
                        walletManageAdapter.notifyItemChanged(position)
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
                        walletManageAdapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.check_all -> {
                isWalletCurrency = false
                binding.tvDefaultWallet.visibility = View.GONE
                binding.lyPullRefresh.visibility = View.VISIBLE
                loadData(walletManageBean)
            }
            R.id.check_eth -> {
                switchRadio(1)
            }
            R.id.check_btc -> {
                switchRadio(3)
            }
            R.id.check_trx -> {
                switchRadio(2)
            }
            R.id.check_sol -> {
                switchRadio(4)
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
            binding.lyPullRefresh.visibility = View.GONE
        } else {
            binding.tvDefaultWallet.visibility = View.GONE
            binding.lyPullRefresh.visibility = View.VISIBLE
            loadData(walletManageCurrencyBean)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                if (isDeletePage) {
                    isDeletePage = false
                    binding.ivManageWalletRight.setBackgroundResource(R.drawable.vector_more_address)
                    binding.ivAddWallet.visibility = View.VISIBLE
                    loadData(walletManageBean)
                } else {
                    backClickSub()
                }
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
        if (isDeletePage) {
            var isAllClickDelete = true
            walletManageBean.iterator().forEach {
                isAllClickDelete = isAllClickDelete && it.isClickDelete
            }
            if (isAllClickDelete) {
                ToastUtils.showToastFree(this, getString(R.string.least_save_wallet))
                isDeletePage = false
                binding.ivManageWalletRight.setBackgroundResource(R.drawable.vector_more_address)
                binding.ivAddWallet.visibility = View.VISIBLE
                loadData(walletManageBean)
            } else {
                deleteConfirmTipsDialog()
            }
        } else {
            binding.checkAll.performClick()
            isDeletePage = true
            binding.ivManageWalletRight.setBackgroundResource(R.drawable.ic_delelet_line)
            binding.ivAddWallet.visibility = View.INVISIBLE
            loadData(walletManageBean)
        }
    }

    private fun backClickSub() {
        if (isFromMine) {
            start(MineActivity::class.java)
        } else {
            when (walletSelleted.walletType) {
                1 -> {
                    start(MainETHActivity::class.java)
                }
                2 -> {
                    start(MainTRXActivity::class.java)
                }
                3 -> {
                    start(MainBTCActivity::class.java)
                }
                4 -> {
                    start(MainSOLActivity::class.java)
                }
            }
        }
    }

    private fun deleteConfirmTipsDialog() {
        showCommonDialog(this, getString(R.string.please_confirm_delete_wallet), confirmListener = {
            btnConfirmSub()
        }, btcListener = {
            isDeletePage = false
            binding.ivManageWalletRight.setBackgroundResource(R.drawable.vector_more_address)
            binding.ivAddWallet.visibility = View.VISIBLE
            loadData(walletManageBean)
        })
    }

    private fun btnConfirmSub() {
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        var delete: ArrayList<Wallet> = arrayListOf()
        walletManageBean.forEach {
            if (it.isClickDelete) {
                detailsList.add(AddressReport.DetailsList(it.address, 2, it.walletType))
                delete.add(it)
            } else {
                detailsList.add(AddressReport.DetailsList(it.address, 0, it.walletType))
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
        SharedPreferencesUtils.saveSecurityString(
            this,
            WALLETSELECTED,
            Gson().toJson(walletManageBean[0])
        )
        saveWalletList = walletManageBean
        SharedPreferencesUtils.saveSecurityString(
            this,
            WALLETINFO,
            Gson().toJson(saveWalletList)
        )
        isDeletePage = false
        binding.ivManageWalletRight.setBackgroundResource(R.drawable.vector_more_address)
        binding.ivAddWallet.visibility = View.VISIBLE
        loadData(walletManageBean)
        if (!SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, "").isNullOrEmpty()) {
            myAllToken = Gson().fromJson(
                SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                object : TypeToken<ArrayList<AllTokenBean>>() {}.type
            )

            val lists = myAllToken.iterator()
            lists.forEach {
                delete.forEach { wallet ->
                    if (it.myCurrency == wallet.address) {
                        lists.remove()
                    }
                }
            }
            SharedPreferencesUtils.saveSecurityString(
                this,
                TOKEN_INFO_NO,
                Gson().toJson(myAllToken)
            )
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
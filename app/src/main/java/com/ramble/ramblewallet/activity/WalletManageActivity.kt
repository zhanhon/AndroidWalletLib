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
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
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
    private lateinit var walletSelleted: Wallet
    private var times = 0
    private var myAllToken: ArrayList<AllTokenBean> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manage)

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
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )
        val list = saveWalletList.iterator()
        list.forEach {
            if (it.address.isEmpty()) {
                list.remove()
            }
        }
        saveWalletList = StringUtils.removeDuplicateByAddress(saveWalletList)
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
        initView()
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        binding.groupButton.check(R.id.check_all)

        if (saveWalletList.isNotEmpty()) {
            walletManageBean = saveWalletList
            loadData(walletManageBean)
        }

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
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        walletManageBean.forEach {
            it.isChoose = it.address == walletSelleted.address
        }
        walletManageAdapter = WalletManageAdapter(walletManageBean, isDeletePage)
        binding.rvMainCurrency.adapter = walletManageAdapter
        walletManageAdapter.setOnItemClickListener { adapter, view, position ->
            if (adapter.getItem(position) is Wallet) {
                SharedPreferencesUtils.saveString(
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
                }
            }
        }
        walletManageAdapter.addChildClickViewIds(R.id.iv_copy_address)
        walletManageAdapter.addChildClickViewIds(R.id.iv_wallet_more)
        walletManageAdapter.addChildClickViewIds(R.id.cl_delete)
        walletManageAdapter.setOnItemChildClickListener { adapter, view, position ->
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
                                    (adapter.getItem(position) as Wallet).publicKey,
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
                                    (adapter.getItem(position) as Wallet).publicKey,
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
    }

    private var isWalletCurrency = false
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.check_all -> {
                isWalletCurrency = false
                binding.tvDefaultWallet.visibility = View.GONE
                binding.lyPullRefresh.visibility = View.VISIBLE
                loadData(walletManageBean)
            }
            R.id.check_eth -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.walletType == 1) {
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
            R.id.check_btc -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.walletType == 3) {
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
            R.id.check_trx -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.walletType == 2) {
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
                    }
                }
            }
            R.id.iv_add_wallet -> {
                startActivity(Intent(this, CreateWalletListActivity::class.java))
            }
            R.id.iv_manage_wallet_right -> {
                if (isDeletePage) {
                    var isAllClickDelete = true
                    walletManageBean.iterator().forEach {
                        isAllClickDelete = isAllClickDelete && it.isClickDelete
                    }
                    if (isAllClickDelete) {
                        toastDefault(getString(R.string.least_save_wallet))
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
            R.id.tv_default_wallet -> {
                startActivity(Intent(this, CreateWalletListActivity::class.java))
            }
        }
    }

    private fun deleteConfirmTipsDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_delete_confirm_tips)
            dialogCenterTheme(window)

            window.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
                isDeletePage = false
                binding.ivManageWalletRight.setBackgroundResource(R.drawable.vector_more_address)
                binding.ivAddWallet.visibility = View.VISIBLE
                loadData(walletManageBean)
            }
            window.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                dialog.dismiss()
            }
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
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
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(walletManageBean[0])
                )
                saveWalletList = walletManageBean
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETINFO,
                    Gson().toJson(saveWalletList)
                )
                isDeletePage = false
                binding.ivManageWalletRight.setBackgroundResource(R.drawable.vector_more_address)
                binding.ivAddWallet.visibility = View.VISIBLE
                loadData(walletManageBean)
                myAllToken = Gson().fromJson(
                    SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
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
                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myAllToken))
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

}
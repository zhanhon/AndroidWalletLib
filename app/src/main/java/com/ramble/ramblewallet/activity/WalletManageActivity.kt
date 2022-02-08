package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.WalletManageAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.WALLETINFO
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.ActivityWalletManageBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.toastDefault
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader


class WalletManageActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener,
    View.OnClickListener {

    private lateinit var binding: ActivityWalletManageBinding
    private var walletManageBean: ArrayList<WalletETH> = arrayListOf()
    private var walletManageCurrencyBean: ArrayList<WalletETH> = arrayListOf()
    private lateinit var walletManageAdapter: WalletManageAdapter
    private var isDeletePage = false
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private lateinit var walletSelleted: WalletETH


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
            object : TypeToken<ArrayList<WalletETH>>() {}.type
        )
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<WalletETH>() {}.type
        )
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

    private fun loadData(walletManageBean: ArrayList<WalletETH>) {
        walletManageBean.forEach {
            it.isChoose = it.address == walletSelleted.address
        }
        walletManageAdapter = WalletManageAdapter(walletManageBean, isDeletePage)
        binding.rvMainCurrency.adapter = walletManageAdapter
        walletManageAdapter.setOnItemClickListener { adapter, view, position ->
            if (adapter.getItem(position) is WalletETH) {
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETSELECTED,
                    Gson().toJson(adapter.getItem(position) as WalletETH)
                )
                when ((adapter.getItem(position) as WalletETH).walletType) {
                    1 -> {
                        startActivity(Intent(this, MainETHActivity::class.java))
                    }
                    2 -> {
                        startActivity(Intent(this, MainTRXActivity::class.java))
                    }
                    0 -> {
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
                    if (adapter.getItem(position) is WalletETH) {
                        ClipboardUtils.copy((adapter.getItem(position) as WalletETH).address)
                    }
                }
                R.id.iv_wallet_more -> {
                    startActivity(Intent(this, WalletMoreOperateActivity::class.java).apply {
                        putExtra(ARG_PARAM1, Gson().toJson(adapter.getItem(position) as WalletETH))
                    })
                }
                R.id.cl_delete -> {
                    if (adapter.getItem(position) is WalletETH) {
                        if ((adapter.getItem(position) as WalletETH).clickDelete) {
                            walletManageBean[position] =
                                WalletETH(
                                    (adapter.getItem(position) as WalletETH).walletName,
                                    (adapter.getItem(position) as WalletETH).walletPassword,
                                    (adapter.getItem(position) as WalletETH).mnemonic,
                                    (adapter.getItem(position) as WalletETH).address,
                                    (adapter.getItem(position) as WalletETH).privateKey,
                                    (adapter.getItem(position) as WalletETH).publicKey,
                                    (adapter.getItem(position) as WalletETH).keystore,
                                    (adapter.getItem(position) as WalletETH).walletType,
                                    false
                                )
                            walletManageAdapter.notifyItemChanged(position)
                        } else {
                            walletManageBean[position] =
                                WalletETH(
                                    (adapter.getItem(position) as WalletETH).walletName,
                                    (adapter.getItem(position) as WalletETH).walletPassword,
                                    (adapter.getItem(position) as WalletETH).mnemonic,
                                    (adapter.getItem(position) as WalletETH).address,
                                    (adapter.getItem(position) as WalletETH).privateKey,
                                    (adapter.getItem(position) as WalletETH).publicKey,
                                    (adapter.getItem(position) as WalletETH).keystore,
                                    (adapter.getItem(position) as WalletETH).walletType,
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
            R.id.check_btc -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.walletType == 0) {
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
                    finish()
                }
            }
            R.id.iv_add_wallet -> {
                startActivity(Intent(this, CreateWalletListActivity::class.java))
            }
            R.id.iv_manage_wallet_right -> {
                if (isDeletePage) {
                    var isAllClickDelete = true
                    walletManageBean.iterator().forEach {
                        isAllClickDelete = isAllClickDelete && it.clickDelete
                    }
                    if (isAllClickDelete) {
                        toastDefault(getString(R.string.least_save_wallet))
                    } else {
                        val list = walletManageBean.iterator()
                        list.forEach {
                            if (it.clickDelete) {
                                list.remove()
                            }
                        }
                        saveWalletList = walletManageBean
                        SharedPreferencesUtils.saveString(
                            this,
                            WALLETINFO,
                            Gson().toJson(saveWalletList)
                        )
                        loadData(walletManageBean)
                    }
                } else {
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

}
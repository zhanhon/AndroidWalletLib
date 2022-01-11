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
import com.ramble.ramblewallet.constant.WALLETINFO
import com.ramble.ramblewallet.databinding.ActivityWalletManageBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manage)
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<WalletETH>>() {}.type
        )

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

        initView()
        initListener()
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
    }

    private fun loadData(walletManageBean: ArrayList<WalletETH>) {
        walletManageAdapter = WalletManageAdapter(walletManageBean, isDeletePage)
        binding.rvMainCurrency.adapter = walletManageAdapter
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
                    startActivity(Intent(this, WalletMoreOperateActivity::class.java))
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
                                    false
                                )
                            walletManageAdapter.notifyItemChanged(position)
                            walletManageAdapter.notifyItemChanged(position)
                        }
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.check_all -> {
                loadData(walletManageBean)
            }
            R.id.check_btc -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.walletType == 0) {
                        walletManageCurrencyBean.add(it)
                    }
                }
                loadData(walletManageCurrencyBean)
            }
            R.id.check_eth -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.walletType == 1) {
                        walletManageCurrencyBean.add(it)
                    }
                }
                loadData(walletManageCurrencyBean)
            }
            R.id.check_trx -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.walletType == 2) {
                        walletManageCurrencyBean.add(it)
                    }
                }
                loadData(walletManageCurrencyBean)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                if (isDeletePage) {
                    isDeletePage = false
                    loadData(walletManageBean)
                } else {
                    finish()
                }
            }
            R.id.iv_add_wallet -> {
                startActivity(Intent(this, CreateWalletListActivity::class.java))
            }
            R.id.iv_manage_wallet_right -> {
                isDeletePage = true
                loadData(walletManageBean)
            }
        }
    }

}
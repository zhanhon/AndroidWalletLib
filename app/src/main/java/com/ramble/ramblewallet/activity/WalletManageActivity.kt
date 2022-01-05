package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.WalletManageAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.WalletManageBean
import com.ramble.ramblewallet.databinding.ActivityWalletManageBinding
import com.ramble.ramblewallet.utils.ClipboardUtils


class WalletManageActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener,
    View.OnClickListener {

    private lateinit var binding: ActivityWalletManageBinding
    private var walletManageBean: ArrayList<WalletManageBean> = arrayListOf()
    private var walletManageCurrencyBean: ArrayList<WalletManageBean> = arrayListOf()
    private lateinit var walletManageAdapter: WalletManageAdapter
    private var isDeletePage = false


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manage)

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
        var a = WalletManageBean()
        a.userName = "大爷"
        a.address = "dfgfdfgpfdgkdpdsfs"
        a.type = 1
        a.isClickDelete = false
        var a1 = WalletManageBean()
        a1.userName = "大爷1"
        a1.address = "dfgfdfgpfdgkdpdsfs1"
        a1.type = 2
        a1.isClickDelete = false
        var a2 = WalletManageBean()
        a2.userName = "大爷2"
        a2.address = "dfgfdfgpfdgkdpdsfs2"
        a2.type = 3
        a2.isClickDelete = false

        var b = WalletManageBean()
        b.userName = "大爷"
        b.address = "hhhhh"
        b.type = 1
        b.isClickDelete = false
        var b1 = WalletManageBean()
        b1.userName = "大爷1"
        b1.address = "hhhhh"
        b1.type = 2
        b1.isClickDelete = false
        var b2 = WalletManageBean()
        b2.userName = "大爷2"
        b2.address = "hhhh"
        b2.type = 3
        b2.isClickDelete = false
        walletManageBean.add(a)
        walletManageBean.add(a1)
        walletManageBean.add(a2)
        walletManageBean.add(b)
        walletManageBean.add(b1)
        walletManageBean.add(b2)
        loadData(walletManageBean)


    }

    private fun initListener() {
        binding.groupButton.setOnCheckedChangeListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivManageWalletRight.setOnClickListener(this)
        binding.ivAddWallet.setOnClickListener(this)
    }

    private fun loadData(walletManageBean: ArrayList<WalletManageBean>) {
        walletManageAdapter = WalletManageAdapter(walletManageBean, isDeletePage)
        binding.rvMainCurrency.adapter = walletManageAdapter
        walletManageAdapter.addChildClickViewIds(R.id.iv_copy_address)
        walletManageAdapter.addChildClickViewIds(R.id.iv_wallet_more)
        walletManageAdapter.addChildClickViewIds(R.id.cl_delete)
        walletManageAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.iv_copy_address -> {
                    if (adapter.getItem(position) is WalletManageBean) {
                        ClipboardUtils.copy((adapter.getItem(position) as WalletManageBean).address)
                    }
                }
                R.id.iv_wallet_more -> {
                    startActivity(Intent(this, WalletMoreOperateActivity::class.java))
                }
                R.id.cl_delete -> {
                    if (adapter.getItem(position) is WalletManageBean) {
                        if ((adapter.getItem(position) as WalletManageBean).isClickDelete) {
                            var abac = WalletManageBean()
                            abac.userName = "大爷1"
                            abac.address = "hhhhh"
                            abac.type = 2
                            abac.isClickDelete = false
                            walletManageBean[position] = abac
                            walletManageAdapter.notifyItemChanged(position)
                        } else {
                            var abac = WalletManageBean()
                            abac.userName = "大爷1"
                            abac.address = "hhhhh"
                            abac.type = 2
                            abac.isClickDelete = true
                            walletManageBean[position] = abac
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
                    if (it.type == 2) {
                        walletManageCurrencyBean.add(it)
                    }
                }
                loadData(walletManageCurrencyBean)
            }
            R.id.check_eth -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.type == 1) {
                        walletManageCurrencyBean.add(it)
                    }
                }
                loadData(walletManageCurrencyBean)
            }
            R.id.check_trx -> {
                walletManageCurrencyBean.clear()
                walletManageBean.forEach {
                    if (it.type == 3) {
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
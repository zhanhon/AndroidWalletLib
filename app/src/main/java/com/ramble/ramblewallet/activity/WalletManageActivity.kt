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
import com.ramble.ramblewallet.eth.Wallet
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manage)
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
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

    private fun loadData(walletManageBean: ArrayList<Wallet>) {
        walletManageAdapter = WalletManageAdapter(walletManageBean, isDeletePage)
        binding.rvMainCurrency.adapter = walletManageAdapter
        walletManageAdapter.addChildClickViewIds(R.id.iv_copy_address)
        walletManageAdapter.addChildClickViewIds(R.id.iv_wallet_more)
        walletManageAdapter.addChildClickViewIds(R.id.cl_delete)
        walletManageAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.iv_copy_address -> {
                    if (adapter.getItem(position) is Wallet) {
                        ClipboardUtils.copy((adapter.getItem(position) as Wallet).address)
                    }
                }
                R.id.iv_wallet_more -> {
                    startActivity(Intent(this, WalletMoreOperateActivity::class.java))
                }
                R.id.cl_delete -> {
                    if (adapter.getItem(position) is Wallet) {
                        if ((adapter.getItem(position) as Wallet).clickDelete) {
                            walletManageBean[position] = Wallet(
                                (adapter.getItem(position) as Wallet).walletName,
                                (adapter.getItem(position) as Wallet).walletPassword,
                                (adapter.getItem(position) as Wallet).mnemonic,
                                (adapter.getItem(position) as Wallet).address,
                                (adapter.getItem(position) as Wallet).privateKey,
                                (adapter.getItem(position) as Wallet).publicKey,
                                (adapter.getItem(position) as Wallet).keystore,
                                (adapter.getItem(position) as Wallet).type,
                                false)
                            walletManageAdapter.notifyItemChanged(position)
                        } else {
                            walletManageBean[position] = Wallet(
                                (adapter.getItem(position) as Wallet).walletName,
                                (adapter.getItem(position) as Wallet).walletPassword,
                                (adapter.getItem(position) as Wallet).mnemonic,
                                (adapter.getItem(position) as Wallet).address,
                                (adapter.getItem(position) as Wallet).privateKey,
                                (adapter.getItem(position) as Wallet).publicKey,
                                (adapter.getItem(position) as Wallet).keystore,
                                (adapter.getItem(position) as Wallet).type,
                                false)
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
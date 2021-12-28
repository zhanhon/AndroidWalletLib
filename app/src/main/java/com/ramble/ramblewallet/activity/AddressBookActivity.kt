package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.AddressBookAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyAddressBean
import com.ramble.ramblewallet.databinding.ActivityAddressBookBinding
import com.ramble.ramblewallet.item.TransferItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils

class AddressBookActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener,
    View.OnClickListener  {

    private lateinit var binding: ActivityAddressBookBinding
    private var myDataBeans: ArrayList<MyAddressBean> = arrayListOf()
    private lateinit var addressBookAdapter: AddressBookAdapter
    private var isDelete=false


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book)
        initView()
        initListener()
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        binding.tvManageWalletTitle.text = getString(R.string.address_page)
        val linearLayoutManagerIcon = LinearLayoutManager(this)
        linearLayoutManagerIcon.orientation = OrientationHelper.VERTICAL
        binding.groupButton.check(R.id.check_all)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = OrientationHelper.VERTICAL
        binding.rvMainCurrency.layoutManager = linearLayoutManager
        myDataBeans = arrayListOf()
        var a = MyAddressBean()
        a.userName = "大爷"
        a.address = "dfgfdfgpfdgkdpdsfs"
        a.type = 1
        var a1 = MyAddressBean()
        a1.userName = "大爷1"
        a1.address = "dfgfdfgpfdgkdpdsfs1"
        a1.type = 2
        var a2 = MyAddressBean()
        a2.userName = "大爷2"
        a2.address = "dfgfdfgpfdgkdpdsfs2"
        a2.type = 3
        myDataBeans.add(a)
        myDataBeans.add(a1)
        myDataBeans.add(a2)
        loadData()
    }

    private fun initListener() {
        binding.groupButton.setOnCheckedChangeListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.add.setOnClickListener(this)
    }

    private fun loadData() {
        addressBookAdapter = AddressBookAdapter(myDataBeans, false)
        binding.rvMainCurrency.adapter = addressBookAdapter
        addressBookAdapter.addChildClickViewIds(R.id.iv_reduce)
        addressBookAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
            when(view.id){
                R.id.iv_menu->{
                    myDataBeans.removeAt(position)
                    addressBookAdapter = AddressBookAdapter(myDataBeans, false)
                    binding.rvMainCurrency.adapter = addressBookAdapter
                    binding.rvMainCurrency.adapter!!.notifyDataSetChanged()
                }
                R.id.iv_reduce->{
                    println("-=-=-=->sav撒旦发射点发射点发范德萨发撒是否")
                    myDataBeans.removeAt(position)
                    addressBookAdapter = AddressBookAdapter(myDataBeans, false)
                    binding.rvMainCurrency.adapter = addressBookAdapter
                    binding.rvMainCurrency.adapter!!.notifyDataSetChanged()
                }
            }
            println("-=-=-=->11111111111111111111")
        })
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.check_all -> {
                myDataBeans = arrayListOf()
                var a = MyAddressBean()
                a.userName = "大爷"
                a.address = "dfgfdfgpfdgkdpdsfs"
                a.type = 1
                var a1 = MyAddressBean()
                a1.userName = "大爷1"
                a1.address = "dfgfdfgpfdgkdpdsfs1"
                a1.type = 2
                var a2 = MyAddressBean()
                a2.userName = "大爷2"
                a2.address = "dfgfdfgpfdgkdpdsfs2"
                a2.type = 3
                myDataBeans.add(a)
                myDataBeans.add(a1)
                myDataBeans.add(a2)
                loadData()
            }
            R.id.check_bt -> {
                myDataBeans = arrayListOf()
                var a = MyAddressBean()
                a.userName = "大爷"
                a.address = "dfgfdfgpfdgkdpdsfs"
                a.type = 2
                var a1 = MyAddressBean()
                a1.userName = "大爷1"
                a1.address = "dfgfdfgpfdgkdpdsfs1"
                a1.type = 2
                var a2 = MyAddressBean()
                a2.userName = "大爷2"
                a2.address = "dfgfdfgpfdgkdpdsfs2"
                a2.type = 2
                myDataBeans.add(a)
                myDataBeans.add(a1)
                myDataBeans.add(a2)
                loadData()
            }
            R.id.check_eth -> {
                myDataBeans = arrayListOf()
                var a = MyAddressBean()
                a.userName = "大爷"
                a.address = "dfgfdfgpfdgkdpdsfs"
                a.type = 1
                var a1 = MyAddressBean()
                a1.userName = "大爷1"
                a1.address = "dfgfdfgpfdgkdpdsfs1"
                a1.type = 1
                var a2 = MyAddressBean()
                a2.userName = "大爷2"
                a2.address = "dfgfdfgpfdgkdpdsfs2"
                a2.type = 1
                myDataBeans.add(a)
                myDataBeans.add(a1)
                myDataBeans.add(a2)
                loadData()
            }
            R.id.check_trx -> {
                myDataBeans = arrayListOf()
                var a = MyAddressBean()
                a.userName = "大爷"
                a.address = "dfgfdfgpfdgkdpdsfs"
                a.type = 3
                var a1 = MyAddressBean()
                a1.userName = "大爷1"
                a1.address = "dfgfdfgpfdgkdpdsfs1"
                a1.type = 3
                var a2 = MyAddressBean()
                a2.userName = "大爷2"
                a2.address = "dfgfdfgpfdgkdpdsfs2"
                a2.type = 3
                myDataBeans.add(a)
                myDataBeans.add(a1)
                myDataBeans.add(a2)
                loadData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.item_transfer -> {//转账详情
                val itemBean = AdapterUtils.getHolder(v).getItem<TransferItem>().data
                // start2(DealDetailActivity::class.java)
            }
            R.id.add -> {
                isDelete = false
                addressBookAdapter = AddressBookAdapter(myDataBeans, false)
                binding.rvMainCurrency.adapter = addressBookAdapter
                binding.rvMainCurrency.adapter!!.notifyDataSetChanged()
            }
            R.id.delete -> {
                isDelete = true
                addressBookAdapter = AddressBookAdapter(myDataBeans, true)
                binding.rvMainCurrency.adapter = addressBookAdapter
                binding.rvMainCurrency.adapter!!.notifyDataSetChanged()
                addressBookAdapter.addChildClickViewIds(R.id.iv_reduce)
                addressBookAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
                    when(view.id){
                        R.id.iv_menu->{
                            myDataBeans.removeAt(position)
                            addressBookAdapter = AddressBookAdapter(myDataBeans, false)
                            binding.rvMainCurrency.adapter = addressBookAdapter
                            binding.rvMainCurrency.adapter!!.notifyDataSetChanged()
                        }
                        R.id.iv_reduce->{
                            println("-=-=-=->sav撒旦发射点发射点发范德萨发撒是否")
                            myDataBeans.removeAt(position)
                            addressBookAdapter = AddressBookAdapter(myDataBeans, false)
                            binding.rvMainCurrency.adapter = addressBookAdapter
                            binding.rvMainCurrency.adapter!!.notifyDataSetChanged()
                        }
                    }
                    println("-=-=-=->11111111111111111111")
                })

            }
        }
    }

}
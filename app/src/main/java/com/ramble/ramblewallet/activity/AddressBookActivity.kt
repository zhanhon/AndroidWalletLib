package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.AddressBookAdapter
import com.ramble.ramblewallet.adapter.AddressBookIconAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.databinding.ActivityAddressBookBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.TransferItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils

class AddressBookActivity : BaseActivity() , RadioGroup.OnCheckedChangeListener,View.OnClickListener{

    private lateinit var binding: ActivityAddressBookBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var addressBookAdapter: AddressBookAdapter


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book)
        initView()
        initListener()
        loadData()

    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        binding.tvManageWalletTitle.text = getString(R.string.address_page)
        val linearLayoutManagerIcon = LinearLayoutManager(this)
        linearLayoutManagerIcon.orientation = OrientationHelper.VERTICAL
        binding.groupButton.check(R.id.check_all)
        addressBookAdapter = AddressBookAdapter(myDataBeans, false)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = OrientationHelper.VERTICAL
        binding.rvMainCurrency.layoutManager = linearLayoutManager
        binding.rvMainCurrency.adapter = addressBookAdapter




    }

    private fun initListener() {
        binding.groupButton.setOnCheckedChangeListener(this)
        binding.ivBack.setOnClickListener(this)
    }

    private fun loadData() {

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.check_all -> {

            }
            R.id.check_bt -> {

            }
            R.id.check_eth -> {

            }
            R.id.check_trx -> {

            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back->finish()
            R.id.item_transfer -> {//转账详情
                val itemBean = AdapterUtils.getHolder(v).getItem<TransferItem>().data
               // start2(DealDetailActivity::class.java)
            }
        }
    }

}
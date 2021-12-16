package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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

class AddressBookActivity : BaseActivity() {

    private lateinit var binding: ActivityAddressBookBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var addressBookIconAdapter: AddressBookIconAdapter
    private lateinit var addressBookAdapter: AddressBookAdapter

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book)

        for (i in 0..10) {
            myDataBeans.add(
                MyDataBean(
                    i + 1,
                    "ricky-$i",
                    "21-$i"
                )
            )
        }

        addressBookIconAdapter = AddressBookIconAdapter(myDataBeans)
        val linearLayoutManagerIcon = LinearLayoutManager(this)
        linearLayoutManagerIcon.orientation = OrientationHelper.VERTICAL
        binding.rvMainCurrencyIcon.layoutManager = linearLayoutManagerIcon
        binding.rvMainCurrencyIcon.adapter = addressBookIconAdapter


        addressBookAdapter = AddressBookAdapter(myDataBeans, false)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = OrientationHelper.VERTICAL
        binding.rvMainCurrency.layoutManager = linearLayoutManager
        binding.rvMainCurrency.adapter = addressBookAdapter


        binding.ivManageWalletRight.setOnClickListener {
            addressBookAdapter = AddressBookAdapter(myDataBeans, true)
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.orientation = OrientationHelper.VERTICAL
            binding.rvMainCurrency.layoutManager = linearLayoutManager
            binding.rvMainCurrency.adapter = addressBookAdapter
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

    }
}
package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyAddressBean
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityAddressBookBinding
import com.ramble.ramblewallet.item.AddressBookItem
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.OnDataSetChanged
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem

class AddressBookActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener,
    View.OnClickListener, OnDataSetChanged {

    private lateinit var binding: ActivityAddressBookBinding
    private var myDataBeans: ArrayList<MyAddressBean> = arrayListOf()
    private var myData: ArrayList<MyAddressBean> = arrayListOf()
    private val adapter = RecyclerAdapter()
    private var pos = -1
    private var bean = MyAddressBean()
    private var isFromTransfer: Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book)
        isFromTransfer = intent.getBooleanExtra(ARG_PARAM1, false)
        initView()
        initListener()
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        binding.tvManageWalletTitle.text = getString(R.string.address_page)
        val linearLayoutManagerIcon = LinearLayoutManager(this)
        linearLayoutManagerIcon.orientation = OrientationHelper.VERTICAL
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = OrientationHelper.VERTICAL
        binding.rvMainCurrency.layoutManager = linearLayoutManager
        binding.rvMainCurrency.adapter = adapter

        if (SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, "").isNotEmpty()) {
            myDataBeans =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, ""),
                    object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                )
        }
        if (SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, "").isNotEmpty()) {
            myData =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, ""),
                    object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                )
        }
        binding.groupButton.check(R.id.check_all)
        loadData()
    }

    private fun initListener() {
        binding.groupButton.setOnCheckedChangeListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.add.setOnClickListener(this)
        adapter.onClickListener = this
        adapter.onDataSetChanged = this
    }

    private fun loadData() {
        ArrayList<AddressBookItem>().also {
            for (item in myDataBeans) {
                it.add(AddressBookItem(item))
            }
            adapter.replaceAll(it.toList())
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.check_all -> {
                myDataBeans = arrayListOf()
                myData.forEach {
                    myDataBeans.add(it)
                }
                loadData()
            }
            R.id.check_bt -> {
                myDataBeans = arrayListOf()
                myData.forEach {
                    if (it.type == 2) {
                        myDataBeans.add(it)
                    }
                }
                loadData()
            }
            R.id.check_eth -> {
                myDataBeans = arrayListOf()
                myData.forEach {
                    if (it.type == 1) {
                        myDataBeans.add(it)
                    }
                }
                loadData()
            }
            R.id.check_trx -> {
                myDataBeans = arrayListOf()
                myData.forEach {
                    if (it.type == 3) {
                        myDataBeans.add(it)
                    }
                }
                loadData()
            }
        }
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_ADDRESS_BOOK_SCAN -> {
                mOnResultsListener!!.onResultsClick(event.data())
            }
            Pie.EVENT_ADDRESS_BOOK_UPDATA -> {
                myDataBeans.set(pos, event.data())
                myData.set(pos, event.data())
                SharedPreferencesUtils.saveString(this, ADDRESS_BOOK_INFO, Gson().toJson(myData))
                adapter.replaceAt(pos, AddressBookItem(event.data()))
                adapter.notifyDataSetChanged()
            }
            Pie.EVENT_ADDRESS_BOOK_ADD -> {
                if (SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, "").isNotEmpty()) {
                    myDataBeans =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, ""),
                            object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                        )
                }
                myDataBeans.add(event.data())

                if (SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, "").isNotEmpty()) {
                    myData =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, ADDRESS_BOOK_INFO, ""),
                            object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                        )
                }
                myData.add(event.data())
                SharedPreferencesUtils.saveString(this, ADDRESS_BOOK_INFO, Gson().toJson(myData))
                loadData()
            }
            else -> return
        }

    }

    /***
     * 接口回调到activity中使用
     */
    var mOnResultsListener: OnResultsListener? = null

    interface OnResultsListener {
        fun onResultsClick(result: String)
    }

    fun setOnResultsListener(mOnResultsListener: OnResultsListener) {
        this.mOnResultsListener = mOnResultsListener
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.iv_menu -> {//更多
                val position = AdapterUtils.getHolder(v).adapterPosition
                pos = position
                val itemBean = AdapterUtils.getHolder(v).getItem<AddressBookItem>().data
                bean = itemBean
                showBottomDialog(this,
                    itemBean.userName,
                    copeListener = View.OnClickListener {//复制
                        ClipboardUtils.copy(itemBean.address)
                    },
                    editListener = View.OnClickListener {//编辑
                        showBottomDialog2(this, itemBean.userName, 1)
                    },
                    delListener = View.OnClickListener {//删除
                        myDataBeans.removeAt(position)
                        myData.removeAt(position)
                        SharedPreferencesUtils.saveString(
                            this,
                            ADDRESS_BOOK_INFO,
                            Gson().toJson(myData)
                        )
                        adapter.remove(position)
                        adapter.notifyDataSetChanged()
                    })
            }
            R.id.iv_reduce -> {//删除
                val position = AdapterUtils.getHolder(v).adapterPosition
                myDataBeans.removeAt(position)
                myData.removeAt(position)
                SharedPreferencesUtils.saveString(this, ADDRESS_BOOK_INFO, Gson().toJson(myData))
                adapter.remove(position)
                adapter.notifyDataSetChanged()
            }
            R.id.add -> {
                showBottomDialog2(this, "", 2)
                if (myDataBeans.isNullOrEmpty()) return
                myDataBeans.forEach {
                    it.isNeedDelete = false
                }
                loadData()
            }
            R.id.delete -> {
                if (myDataBeans.isNullOrEmpty()) return
                myDataBeans.forEach {
                    it.isNeedDelete = true
                }
                loadData()
            }
            R.id.item_address -> {
                if (isFromTransfer) {
                    val holder = AdapterUtils.getHolder(v)
                    when (val item = holder.getItem<SimpleRecyclerItem>()) {
                        is AddressBookItem -> {
                            startActivity(Intent(this, TransferActivity::class.java).apply {
                                putExtra(ARG_PARAM1, item.data.address)
                                putExtra(ARG_PARAM2, intent.getStringExtra(ARG_PARAM2))
                                putExtra(ARG_PARAM3, intent.getBooleanExtra(ARG_PARAM3, false))
                                putExtra(ARG_PARAM4, intent.getStringExtra(ARG_PARAM4))
                            })
                        }
                    }
                }
            }
        }
    }

    override fun apply(count: Int) {

    }

}
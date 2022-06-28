package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.FaqInfos
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.ARG_PARAM3
import com.ramble.ramblewallet.constant.ARG_PARAM4
import com.ramble.ramblewallet.databinding.ActivityHelpBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.item.Help
import com.ramble.ramblewallet.network.faqInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.TimeUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.QuickItemDecoration
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem

/**
 * 时间　: 2021/12/17 8:45
 * 作者　: potato
 * 描述　: 帮助中心
 */
class HelpActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHelpBinding
    private val adapter = RecyclerAdapter()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help)
        initView()
        initListener()
        loadData()
    }

    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.help_feedback)
        LinearLayoutManager(this).apply {
            binding.recycler.layoutManager = this
        }
        binding.recycler.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_04)
                .build()
        )
        binding.recycler.adapter = adapter
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        val lang = TimeUtils.dateToLang(this)
        mApiService.getFaqInfos(FaqInfos.Req(lang).toApiRequest(faqInfoUrl)).applyIo().subscribe({
            if (it.code() == 1) {
                it.data()?.let { data ->
                    adapter.add(Help.Header(getString(R.string.help_door_sister)))
                    ArrayList<SimpleRecyclerItem>().apply {
                        data.noviceList.forEach { o -> this.add(Help.FaqTypeList(o)) }
                        adapter.addAll(this.toList())
                    }
                    adapter.add(Help.Header(getString(R.string.help_sever_sister)))
                    ArrayList<SimpleRecyclerItem>().apply {
                        data.categoryList.forEach { o -> this.add(Help.HotFaqList(o)) }
                        adapter.addAll(this.toList())
                    }

                }
            }
        }, {})
    }


    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        adapter.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.item_help_todo -> {
                when (val item = AdapterUtils.getHolder(v).getItem<SimpleRecyclerItem>()) {
                    is Help.FaqTypeList -> {
                        start(MsgDetailsActivity::class.java, Bundle().also {
                            it.putString(ARG_PARAM1, item.data.title)
                            it.putString(ARG_PARAM2, item.data.content)
                            it.putString(ARG_PARAM3, item.data.createTime)
                            it.putInt(ARG_PARAM4, 1)
                        })
                    }
                    is Help.HotFaqList -> {
                        start(HelpFaqActivity::class.java, Bundle().also {
                            it.putString(ARG_PARAM1, item.data.name)
                            it.putInt(ARG_PARAM2, item.data.id)
                        })
                    }
                }
            }
        }
    }
}
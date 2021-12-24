package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.GetFaqInfos
import com.ramble.ramblewallet.databinding.ActivityHelpBinding
import com.ramble.ramblewallet.item.Help
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help)
        initView()
        initListener()
        loadData()
    }

    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.help_center)
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

    private fun loadData() {
        var hotFaqLists= arrayListOf<GetFaqInfos.HotFaqList>()
        var hotFaqList = GetFaqInfos.HotFaqList()
        hotFaqList.title="撒大苏打实打实方式"
        var hotFaqList1 = GetFaqInfos.HotFaqList()
        hotFaqList1.title="是否是v发"
        var hotFaqList2 = GetFaqInfos.HotFaqList()
        hotFaqList2.title="i离开英国法国的"
        hotFaqLists.add(hotFaqList)
        hotFaqLists.add(hotFaqList1)
        hotFaqLists.add(hotFaqList2)
        var getFaqInfos=GetFaqInfos()
        getFaqInfos.hotFaqLists=hotFaqLists
        var faqs= arrayListOf<GetFaqInfos.FaqTypeList>()
        var faq = GetFaqInfos.FaqTypeList()
        faq.typeName="232323232323232"
        var faq1 = GetFaqInfos.FaqTypeList()
        faq1.typeName="ssddfdfdfdfdf"
        var faq2 = GetFaqInfos.FaqTypeList()
        faq2.typeName="gtgtgtgtgtgtggt"
        faqs.add(faq)
        faqs.add(faq1)
        faqs.add(faq2)
        getFaqInfos.faqTypeLists=faqs
        adapter.add(Help.Header(getString(R.string.help_door_sister)))
        ArrayList<SimpleRecyclerItem>().apply {
            getFaqInfos.hotFaqLists.forEach { o -> this.add(Help.HotFaqList(o)) }
            adapter.addAll(this.toList())
        }
        adapter.add(Help.Header(getString(R.string.help_sever_sister)))
        ArrayList<SimpleRecyclerItem>().apply {
            getFaqInfos.faqTypeLists.forEach { o -> this.add(Help.FaqTypeList(o)) }
            adapter.addAll(this.toList())
        }
//        if (lock) {
//            return
//        }
//        lock = true
//        model.getFaqInfos(GetFaqInfos.Req()).subscribe({
//            if (it.status()) {
//                lock = false
//                adapter.add(Help.Header("热门问题"))
//                ArrayList<SimpleRecyclerItem>().apply {
//                    it.data()!!.hotFaqLists.forEach { o -> this.add(Help.HotFaqList(o)) }
//                    adapter.addAll(this.toList())
//                }
//                adapter.add(Help.Header("问题分类"))
//                ArrayList<SimpleRecyclerItem>().apply {
//                    it.data()!!.faqTypeLists.forEach { o -> this.add(Help.FaqTypeList(o)) }
//                    adapter.addAll(this.toList())
//                }
//            } else {
//                toastDefault(it.message())
//            }
//        }, {
//            lock = false
//            it.toast()
//        }
//        ).addTo(onDestroyComposite)
    }


    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        adapter.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.item_help_todo -> {
                val holder = AdapterUtils.getHolder(v)
                when (val item = holder.getItem<SimpleRecyclerItem>()) {
                    is Help.FaqTypeList -> {
//                        RxViewModel.globe.helpTitle = item.data.typeName!!
//                        RxViewModel.globe.FaqTypeListId = item.data.id!!
//                        this.start2(
//                            UniversalActivity::class.java,
//                            Bundle().also { it.putInt(ARG_PARAM1, 21) })
                    }
                    is Help.HotFaqList -> {
//                        RxViewModel.globe.helpTitle = item.data.title!!
//                        RxViewModel.globe.FaqTypeListId = item.data.id!!
//                        this.start2(
//                            UniversalActivity::class.java,
//                            Bundle().also { it.putInt(ARG_PARAM1, 22) })
                    }
                }
            }
        }
    }
}
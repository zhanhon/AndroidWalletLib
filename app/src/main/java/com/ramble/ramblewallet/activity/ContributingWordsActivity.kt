package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.ContributingWordsAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.databinding.ActivityContributingWordsBinding
import com.ramble.ramblewallet.eth.MnemonicUtils
import com.ramble.ramblewallet.eth.utils.ChineseTraditional


class ContributingWordsActivity : BaseActivity() {

    private lateinit var binding: ActivityContributingWordsBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var contributingWordsAdapter: ContributingWordsAdapter
    private lateinit var mnemonicETH: ArrayList<String>
    private lateinit var mnemonicETHOriginal: ArrayList<String>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words)

        // 生成钱包助记词
        val mnemonicString: String =
            MnemonicUtils.generateMnemonicCustom(ChineseTraditional.INSTANCE)
        mnemonicETH = mnemonicString.split(" ") as ArrayList<String>
        mnemonicETHOriginal = mnemonicString.split(" ") as ArrayList<String>
        println("-=-=-=->before:$mnemonicETH")
        mnemonicETH.forEachIndexed { index, element ->
            myDataBeans.add(MyDataBean(index + 1, element, ""))
        }

        mnemonicETH.shuffle()
        println("-=-=-=->after:$mnemonicETH")

        contributingWordsAdapter = ContributingWordsAdapter(myDataBeans)
        binding.rvContributingWords.adapter = contributingWordsAdapter

        binding.btnContributingWordsConfirm.setOnClickListener {
            startActivity(Intent(this, ContributingWordsConfirmActivity::class.java).apply {
                putStringArrayListExtra(ARG_PARAM1, mnemonicETH)
                putStringArrayListExtra(ARG_PARAM2, mnemonicETHOriginal)
            })
        }
    }
}
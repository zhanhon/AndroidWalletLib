package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.ContributingWordsAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityContributingWordsBinding
import com.ramble.ramblewallet.eth.MnemonicUtils
import com.ramble.ramblewallet.eth.utils.ChineseSimplified
import com.ramble.ramblewallet.eth.utils.ChineseTraditional
import com.ramble.ramblewallet.eth.utils.English
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils


class ContributingWordsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityContributingWordsBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var contributingWordsAdapter: ContributingWordsAdapter
    private lateinit var mnemonicETH: ArrayList<String>
    private lateinit var mnemonicETHOriginal: ArrayList<String>
    private lateinit var mnemonicString: String
    private lateinit var walletName: String
    private lateinit var walletPassword: String

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words)
        walletName = intent.getStringExtra(ARG_PARAM1)
        walletPassword = intent.getStringExtra(ARG_PARAM2)
        initData()

        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnOneCopy.setOnClickListener(this)
        binding.btnSkipThis.setOnClickListener(this)
    }

    private fun initData() {
        if (SharedPreferencesUtils.getString(this, LANGUAGE, CN) == EN) {
            binding.llChinese.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
        binding.vChinese.setBackgroundResource(R.color.color_9598AA)
        // 生成钱包助记词
        mnemonicString = MnemonicUtils.generateMnemonicCustom(English.INSTANCE)
        createContributingWordsPage(mnemonicString)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_english -> {
                binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
                binding.vChinese.setBackgroundResource(R.color.color_9598AA)
                // 生成钱包助记词
                mnemonicString = MnemonicUtils.generateMnemonicCustom(English.INSTANCE)
                createContributingWordsPage(mnemonicString)
            }
            R.id.ll_chinese -> {
                binding.vEnglish.setBackgroundResource(R.color.color_9598AA)
                binding.vChinese.setBackgroundResource(R.color.color_3F5E94)
                if (SharedPreferencesUtils.getString(this, LANGUAGE, CN) == CN) {
                    // 生成钱包助记词
                    mnemonicString =
                        MnemonicUtils.generateMnemonicCustom(ChineseSimplified.INSTANCE)
                    createContributingWordsPage(mnemonicString)
                } else {
                    // 生成钱包助记词
                    mnemonicString =
                        MnemonicUtils.generateMnemonicCustom(ChineseTraditional.INSTANCE)
                    createContributingWordsPage(mnemonicString)
                }
            }
            R.id.btn_one_copy -> {
                ClipboardUtils.copy(mnemonicString)
            }
            R.id.btn_skip_this -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun createContributingWordsPage(mnemonicString: String) {
        myDataBeans.clear()
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
                putExtra(ARG_PARAM3, walletName)
                putExtra(ARG_PARAM4, walletPassword)
            })
        }
    }
}
package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.ContributingWordsConfirmAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.databinding.ActivityContributingWordsConfirmBinding
import com.ramble.ramblewallet.eth.WalletManager
import com.ramble.ramblewallet.eth.WalletManager.isETHValidAddress


class ContributingWordsConfirmActivity : BaseActivity() {

    private lateinit var contributingWordsConfirmAdapter: ContributingWordsConfirmAdapter
    private lateinit var binding: ActivityContributingWordsConfirmBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private var mnemonicETHShuffled: ArrayList<String> = arrayListOf()
    private var mnemonicETHOriginal: ArrayList<String> = arrayListOf()
    private var mnemonicETHChoose: ArrayList<String> = arrayListOf()
    private lateinit var walletETHString: String

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words_confirm)
        mnemonicETHShuffled = intent.getStringArrayListExtra(ARG_PARAM1)
        mnemonicETHOriginal = intent.getStringArrayListExtra(ARG_PARAM2)
        initMnmonicETH()
        mnmonicETHClick()
        binding.btnContributingWordsCompleted.setOnClickListener {
            if (mnemonicETHChoose == mnemonicETHOriginal) {
                mnemonicETHChoose.forEach {
                    walletETHString = "$it "
                }
                //1、助记词生成地址
                var walletETHAddress: String = WalletManager.generateAddress(walletETHString.trim())
                println("-=-=-=->wallestETHAddress:$walletETHAddress")
                //2、之后地址校验
                var isValidSuccess = isETHValidAddress(walletETHAddress)
                if (isValidSuccess) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                println("-=-=-=->isValidSuccess:$isValidSuccess")
            }
        }
    }

    private fun initMnmonicETH() {
        binding.tvContributingWordsName1.text = mnemonicETHShuffled[0]
        binding.tvContributingWordsName2.text = mnemonicETHShuffled[1]
        binding.tvContributingWordsName3.text = mnemonicETHShuffled[2]
        binding.tvContributingWordsName4.text = mnemonicETHShuffled[3]
        binding.tvContributingWordsName5.text = mnemonicETHShuffled[4]
        binding.tvContributingWordsName6.text = mnemonicETHShuffled[5]
        binding.tvContributingWordsName7.text = mnemonicETHShuffled[6]
        binding.tvContributingWordsName8.text = mnemonicETHShuffled[7]
        binding.tvContributingWordsName9.text = mnemonicETHShuffled[8]
        binding.tvContributingWordsName10.text = mnemonicETHShuffled[9]
        binding.tvContributingWordsName11.text = mnemonicETHShuffled[10]
        binding.tvContributingWordsName12.text = mnemonicETHShuffled[11]
    }

    private fun mnmonicETHClick() {
        binding.tvContributingWordsName1.setOnClickListener {
            binding.tvContributingWordsName1.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(0, binding.tvContributingWordsName1.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName1.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName2.setOnClickListener {
            binding.tvContributingWordsName2.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(1, binding.tvContributingWordsName2.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName2.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName3.setOnClickListener {
            binding.tvContributingWordsName3.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(2, binding.tvContributingWordsName3.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName3.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName4.setOnClickListener {
            binding.tvContributingWordsName4.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(3, binding.tvContributingWordsName4.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName4.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName5.setOnClickListener {
            binding.tvContributingWordsName5.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(4, binding.tvContributingWordsName5.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName5.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName6.setOnClickListener {
            binding.tvContributingWordsName6.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(5, binding.tvContributingWordsName6.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName6.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName7.setOnClickListener {
            binding.tvContributingWordsName7.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(6, binding.tvContributingWordsName7.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName7.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName8.setOnClickListener {
            binding.tvContributingWordsName8.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(7, binding.tvContributingWordsName8.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName8.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName9.setOnClickListener {
            binding.tvContributingWordsName9.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(8, binding.tvContributingWordsName9.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName9.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName10.setOnClickListener {
            binding.tvContributingWordsName10.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(9, binding.tvContributingWordsName10.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName10.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName11.setOnClickListener {
            binding.tvContributingWordsName11.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(10, binding.tvContributingWordsName11.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName11.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName12.setOnClickListener {
            binding.tvContributingWordsName12.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(11, binding.tvContributingWordsName12.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName12.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            contributingWordsConfirmClick()
        }
    }


    private fun contributingWordsConfirmClick() {
        contributingWordsConfirmAdapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                1 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                2 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                3 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                4 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                5 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                6 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                7 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                8 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                9 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                10 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                11 -> {
                    position(myDataBeans[position].index)
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun position(position: Int) {
        when (position) {
            0 -> {
                binding.tvContributingWordsName1.visibility = View.VISIBLE
            }
            1 -> {
                binding.tvContributingWordsName2.visibility = View.VISIBLE
            }
            2 -> {
                binding.tvContributingWordsName3.visibility = View.VISIBLE
            }
            3 -> {
                binding.tvContributingWordsName4.visibility = View.VISIBLE
            }
            4 -> {
                binding.tvContributingWordsName5.visibility = View.VISIBLE
            }
            5 -> {
                binding.tvContributingWordsName6.visibility = View.VISIBLE
            }
            6 -> {
                binding.tvContributingWordsName7.visibility = View.VISIBLE
            }
            7 -> {
                binding.tvContributingWordsName8.visibility = View.VISIBLE
            }
            8 -> {
                binding.tvContributingWordsName9.visibility = View.VISIBLE
            }
            9 -> {
                binding.tvContributingWordsName10.visibility = View.VISIBLE
            }
            10 -> {
                binding.tvContributingWordsName11.visibility = View.VISIBLE
            }
            11 -> {
                binding.tvContributingWordsName12.visibility = View.VISIBLE
            }
        }
    }
}
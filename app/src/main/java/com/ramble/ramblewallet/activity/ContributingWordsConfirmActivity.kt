package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.ContributingWordsConfirmAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.custom.AutoLineFeedLayoutManager
import com.ramble.ramblewallet.databinding.ActivityContributingWordsConfirmBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isETHValidAddress
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo


class ContributingWordsConfirmActivity : BaseActivity(), View.OnClickListener {

    private lateinit var contributingWordsConfirmAdapter: ContributingWordsConfirmAdapter
    private lateinit var binding: ActivityContributingWordsConfirmBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private var mnemonicETHShuffled: ArrayList<String> = arrayListOf()
    private var mnemonicETHOriginal: ArrayList<String> = arrayListOf()
    private var mnemonicETHChoose: ArrayList<String> = arrayListOf()
    private var walletETHString: String = ""
    private lateinit var walletName: String
    private lateinit var walletPassword: String
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var currentTab = ""
    private lateinit var mnemonicETH: ArrayList<String>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words_confirm)
        mnemonicETH = intent.getStringArrayListExtra(ARG_PARAM1)
        walletName = intent.getStringExtra(ARG_PARAM2)
        walletPassword = intent.getStringExtra(ARG_PARAM3)
        currentTab = intent.getStringExtra(ARG_PARAM4)

        initData()
        initMnmonicETH()
        mnmonicETHClick()

        binding.llEnglish.setOnClickListener(this)
        binding.llChinese.setOnClickListener(this)
        binding.btnContributingWordsCompleted.setOnClickListener {
            println("-=-=-=->mnemonicETHChoose:${mnemonicETHChoose}")
            if (mnemonicETHChoose == mnemonicETHOriginal) {
                mnemonicETHChoose.forEach {
                    walletETHString = "$walletETHString$it "
                }
                var walletETH: WalletETH = WalletETHUtils.generateWalletByMnemonic(
                    walletName,
                    walletPassword,
                    walletETHString.trim()
                )
                println("-=-=-=->wallestETHAddress:${walletETH.address}")
                println("-=-=-=->walletETHMnemonic:${walletETH.mnemonic}")
                println("-=-=-=->walletETHPrivateKey:${walletETH.privateKey}")
                println("-=-=-=->walletETHKeystore:${walletETH.keystore}")

                putAddress(walletETH)

                if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                    saveWalletList =
                        Gson().fromJson(
                            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                            object : TypeToken<ArrayList<WalletETH>>() {}.type
                        )
                }
                saveWalletList.add(walletETH)
                println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))


                //2、之后地址校验
                var isValidSuccess = isETHValidAddress(walletETH.address)
                if (isValidSuccess) {
                    println("-=-=-=->isValidSuccess:$isValidSuccess")
                    startActivity(Intent(this, MainETHActivity::class.java))
                }

            }
        }
    }

    private fun initData() {
        when (currentTab) {
            "english" -> {
                binding.vEnglish.setBackgroundResource(R.color.color_3F5E94)
                binding.vChinese.setBackgroundResource(R.color.color_9598AA)
                mnemonicETHOriginal = mnemonicETH[0].split(" ") as ArrayList<String>
                mnemonicETHShuffled = mnemonicETH[0].split(" ") as ArrayList<String>
                println("-=-=-=->before:$mnemonicETH")
                mnemonicETHShuffled.shuffle()
                println("-=-=-=->after:$mnemonicETHShuffled")
            }
            "chinese" -> {
                binding.vEnglish.setBackgroundResource(R.color.color_9598AA)
                binding.vChinese.setBackgroundResource(R.color.color_3F5E94)
                mnemonicETHOriginal = mnemonicETH[1].split(" ") as ArrayList<String>
                mnemonicETHShuffled = mnemonicETH[1].split(" ") as ArrayList<String>
                println("-=-=-=->before:$mnemonicETH")
                mnemonicETHShuffled.shuffle()
                println("-=-=-=->after:$mnemonicETHShuffled")
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_english -> {
                currentTab = "english"
                initData()
                initMnmonicETH()
            }
            R.id.ll_chinese -> {
                currentTab = "chinese"
                initData()
                initMnmonicETH()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun putAddress(walletETHKeyStore: WalletETH) {
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletETHKeyStore.address, 1)) //ETH
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    putAddress(walletETHKeyStore)
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
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
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName2.setOnClickListener {
            binding.tvContributingWordsName2.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(1, binding.tvContributingWordsName2.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName2.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName3.setOnClickListener {
            binding.tvContributingWordsName3.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(2, binding.tvContributingWordsName3.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName3.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName4.setOnClickListener {
            binding.tvContributingWordsName4.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(3, binding.tvContributingWordsName4.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName4.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName5.setOnClickListener {
            binding.tvContributingWordsName5.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(4, binding.tvContributingWordsName5.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName5.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName6.setOnClickListener {
            binding.tvContributingWordsName6.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(5, binding.tvContributingWordsName6.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName6.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName7.setOnClickListener {
            binding.tvContributingWordsName7.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(6, binding.tvContributingWordsName7.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName7.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName8.setOnClickListener {
            binding.tvContributingWordsName8.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(7, binding.tvContributingWordsName8.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName8.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName9.setOnClickListener {
            binding.tvContributingWordsName9.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(8, binding.tvContributingWordsName9.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName9.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName10.setOnClickListener {
            binding.tvContributingWordsName10.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(9, binding.tvContributingWordsName10.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName10.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName11.setOnClickListener {
            binding.tvContributingWordsName11.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(10, binding.tvContributingWordsName11.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName11.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
        binding.tvContributingWordsName12.setOnClickListener {
            binding.tvContributingWordsName12.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(11, binding.tvContributingWordsName12.text.toString(), ""))
            mnemonicETHChoose.add(binding.tvContributingWordsName12.text.toString())
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.layoutManager = AutoLineFeedLayoutManager()
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmClick()
        }
    }


    private fun contributingWordsConfirmClick() {
        contributingWordsConfirmAdapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                1 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                2 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                3 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                4 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                5 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                6 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                7 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                8 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                9 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                10 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
                    contributingWordsConfirmAdapter.remove(myDataBeans[position])
                    contributingWordsConfirmAdapter.notifyDataSetChanged()
                }
                11 -> {
                    position(myDataBeans[position].index)
                    mnemonicETHChoose.remove(mnemonicETHChoose[position])
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
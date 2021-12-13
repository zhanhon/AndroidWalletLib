package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.ContributingWordsConfirmAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.databinding.ActivityContributingWordsConfirmBinding

class ContributingWordsConfirmActivity : BaseActivity() {

    private lateinit var contributingWordsConfirmAdapter: ContributingWordsConfirmAdapter
    private lateinit var binding: ActivityContributingWordsConfirmBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributing_words_confirm)

        binding.tvContributingWordsName1.text = "ricky1"
        binding.tvContributingWordsName2.text = "ricky2"
        binding.tvContributingWordsName3.text = "ricky3"
        binding.tvContributingWordsName4.text = "ricky4"
        binding.tvContributingWordsName5.text = "ricky5"
        binding.tvContributingWordsName6.text = "ricky6"
        binding.tvContributingWordsName7.text = "ricky7"
        binding.tvContributingWordsName8.text = "ricky8"
        binding.tvContributingWordsName9.text = "ricky9"
        binding.tvContributingWordsName10.text = "ricky10"
        binding.tvContributingWordsName11.text = "ricky11"
        binding.tvContributingWordsName12.text = "ricky12"

        binding.tvContributingWordsName1.setOnClickListener {
            binding.tvContributingWordsName1.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(0, "ricky-1", "21-1"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName2.setOnClickListener {
            binding.tvContributingWordsName2.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(1, "ricky-2", "21-2"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName3.setOnClickListener {
            binding.tvContributingWordsName3.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(2, "ricky-3", "21-3"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName4.setOnClickListener {
            binding.tvContributingWordsName4.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(3, "ricky-4", "21-4"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName5.setOnClickListener {
            binding.tvContributingWordsName5.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(4, "ricky-5", "21-5"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName6.setOnClickListener {
            binding.tvContributingWordsName6.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(5, "ricky-6", "21-6"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName7.setOnClickListener {
            binding.tvContributingWordsName7.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(6, "ricky-7", "21-7"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName8.setOnClickListener {
            binding.tvContributingWordsName8.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(7, "ricky-8", "21-8"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName9.setOnClickListener {
            binding.tvContributingWordsName9.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(8, "ricky-9", "21-9"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName10.setOnClickListener {
            binding.tvContributingWordsName10.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(9, "ricky-10", "21-10"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName11.setOnClickListener {
            binding.tvContributingWordsName11.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(10, "ricky-11", "21-11"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }
        binding.tvContributingWordsName12.setOnClickListener {
            binding.tvContributingWordsName12.visibility = View.INVISIBLE
            binding.tvContributingWordsConfirmTips.visibility = View.GONE

            myDataBeans.add(MyDataBean(11, "ricky-12", "21-12"))
            contributingWordsConfirmAdapter = ContributingWordsConfirmAdapter(myDataBeans)
            binding.rvContributingWords.adapter = contributingWordsConfirmAdapter
            contributingWordsConfirmAdapter.notifyDataSetChanged()
            onClick()
        }

    }

    private fun onClick() {
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
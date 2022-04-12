package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.PrivacyPolicyInfo
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMsgDetailsBinding
import com.ramble.ramblewallet.helper.formatHTML
import com.ramble.ramblewallet.helper.getExtras
import com.ramble.ramblewallet.network.getPrivacyInfo
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.LanguageSetting
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo

/**
 * 时间　: 2021/12/16 13:54
 * 作者　: potato
 * 描述　: 消息详情
 */
class MsgDetailsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMsgDetailsBinding
    private var title = ""
    private var content = ""
    private var createTime = ""
    private var typeText = 0
    private var id = 0
    private var list: ArrayList<Int> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_msg_details)
        binding.web.display
        setLanguage()
        title = getExtras().getString(ARG_PARAM1, "")
        content = getExtras().getString(ARG_PARAM2, "")
        createTime = getExtras().getString(ARG_PARAM3, "")
        typeText = getExtras().getInt(ARG_PARAM4, 0)
        id = getExtras().getInt(ARG_PARAM5, 0)
        initView()
        initListener()
    }

    private fun initView() {
        when (typeText) {
            1 -> {
                binding.ivRight.visibility = View.GONE
                binding.tvMineTitle.text = getString(R.string.help_center_content)
                isCheckContent(content, title)
            }
            2, 22 -> {
                binding.ivRight.visibility = View.GONE
                if (typeText == 2) {
                    binding.tvMineTitle.text = getString(R.string.message_details)
                    listData()
                } else {
                    binding.tvMineTitle.text = getString(R.string.announcement_content)
                    listData2()
                }
                isCheckContent(content, title)
            }
            3 -> {
                binding.ivRight.visibility = View.GONE
                binding.tvMineTitle.text = getString(R.string.privacy_statement)
                getData()
            }
            4 -> {
                binding.ivRight.visibility = View.GONE
                binding.tvMineTitle.text = getString(R.string.service_agreement)
                getData()
            }
            else -> {
                binding.tvMineTitle.text = getString(R.string.message_details)
                isCheckContent(content, title)
            }
        }
    }

    private fun listData() {
        if (id != 0) {
            list = if (SharedPreferencesUtils.getString(
                    this,
                    READ_ID,
                    ""
                ).isNotEmpty()
            ) {
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, READ_ID, ""),
                    object : TypeToken<ArrayList<Int>>() {}.type
                )
            } else {
                arrayListOf()
            }
            if (list.isNotEmpty()) {
                if (!list.contains(id)) {
                    list.add(id)
                }
            } else {
                list.add(id)
            }
            SharedPreferencesUtils.saveString(this, READ_ID, Gson().toJson(list))
        }
    }

    private fun listData2() {
        if (id != 0) {
            list = if (SharedPreferencesUtils.getString(
                    this,
                    READ_ID_NEW,
                    ""
                ).isNotEmpty()
            ) {
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, READ_ID_NEW, ""),
                    object : TypeToken<ArrayList<Int>>() {}.type
                )
            } else {
                arrayListOf()
            }
            if (list.isNotEmpty()) {
                if (!list.contains(id)) {
                    list.add(id)
                }
            } else {
                list.add(id)
            }

            SharedPreferencesUtils.saveString(this, READ_ID_NEW, Gson().toJson(list))
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
    }

    @SuppressLint("CheckResult")
    private fun getData() {
        var req = PrivacyPolicyInfo.Req()
        req.lang = when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                1
            }
            TW -> {
                2
            }
            else -> {
                3
            }
        }
        req.type = when (typeText) {
            3 -> "privacy"
            else -> "service"
        }
        mApiService.getPrivacyPolicy(req.toApiRequest(getPrivacyInfo)).applyIo().subscribe({
            if (it.code() == 1) {
                content = it.data()!!.content
                title = it.data()!!.title
                createTime = it.data()!!.createTime
                isCheckContent(content, title)
            } else {
                println("==================>getTransferInfo1:${it.message()}")
            }
        }, {
            println("==================>getTransferInfo1:${it.printStackTrace()}")
        }
        )
    }

    private fun isCheckContent(content: String, title: String) {
        binding.title1.text = if (title.length >= 30) {
            title.subSequence(0, 30).toString() + "..."
        } else {
            title
        }
        binding.time2.text =
            if (createTime.isNotEmpty()) getString(R.string.release_date) + createTime else createTime
        if (content.contains("<") && content.contains(">")) {
            binding.web.visibility = View.VISIBLE
            binding.tvContent.visibility = View.GONE
            binding.web.loadDataWithHtml(formatHTML(content, title))
        } else {
            binding.tvContent.text = content
        }
    }

    private fun setLanguage() {
        when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                LanguageSetting.setLanguage(this, 1)
            }
            TW -> {
                LanguageSetting.setLanguage(this, 2)
            }
            EN -> {
                LanguageSetting.setLanguage(this, 3)
            }
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
        }
    }
}
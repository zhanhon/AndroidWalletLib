package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.PrivacyPolicyInfo
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMsgDetailsBinding
import com.ramble.ramblewallet.helper.formatHTML
import com.ramble.ramblewallet.helper.getExtras
import com.ramble.ramblewallet.network.getPrivacyInfo
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.wight.HtmlWebView
import java.util.ArrayList

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
    private var id=0
    private var list = mutableListOf<Any?>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_msg_details)
        title = getExtras().getString(ARG_PARAM1, "")
        content = getExtras().getString(ARG_PARAM2, "")
        createTime = getExtras().getString(ARG_PARAM3, "")
        typeText = getExtras().getInt(ARG_PARAM4, 0)
        id== getExtras().getInt(ARG_PARAM5, 0)
        when (typeText) {
            1 -> {
                binding.ivRight.visibility = View.GONE
                binding.tvMineTitle.text = getString(R.string.help_center_content)
                isCheckContent(content, title)
            }
            2 -> {
                binding.ivRight.visibility = View.GONE
                binding.tvMineTitle.text = getString(R.string.message_details)
                isCheckContent(content, title)
                if (id!=0){
                    list = if (SharedPreferencesUtils.getString(
                            this,
                            READ_ID,
                            ""
                        ).isNotEmpty()
                    ) {
                        SharedPreferencesUtils.String2SceneList(
                            SharedPreferencesUtils.getString(
                                this,
                                READ_ID,
                                ""
                            )
                        )
                    } else {
                        mutableListOf()
                    }
                    if (list.isNotEmpty()) {
                        if (!list.contains(id)) {
                            list.add(id)
                        }
                    } else {
                        list.add(id)
                    }
                    var addId = SharedPreferencesUtils.SceneList2String(list)
                    SharedPreferencesUtils.saveString(this, READ_ID, addId)
                }
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
        initListener()
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
                content=it.data()!!.content
                title=it.data()!!.title
                createTime=it.data()!!.createTime
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
        binding.title1.text = title
        binding.time2.text = if (createTime.isNotEmpty()) "发布日期：$createTime" else createTime
        if (content.contains("<") && content.contains(">")) {
            binding.web.visibility = View.VISIBLE
            binding.tvContent.visibility = View.GONE

            binding.web.setPlaceholderImage("loading_default_image.png")
            binding.web.setOnHtmlWebViewListener(object : HtmlWebView.OnHtmlWebViewListener {
                override fun onPageStarted() {}
                override fun onPageFinished() {}

                @SuppressLint("InflateParams")
                override fun onClickImage(url: String?, position: Int) {
                    var imagePath: String = if (url!!.contains("http")) url else ""
//                    RxBus.emitEvent(Pie.EVENT_NOTICE_IAMGE_SELECT, imagePath)

                }
            })
            binding.web.loadDataWithHtml(formatHTML(content, title))
        } else {
            binding.tvContent.text = content
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.iv_mine_right -> {


            }
        }
    }
}
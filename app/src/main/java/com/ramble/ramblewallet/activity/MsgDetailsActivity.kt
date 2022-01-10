package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.ARG_PARAM3
import com.ramble.ramblewallet.constant.ARG_PARAM4
import com.ramble.ramblewallet.databinding.ActivityMsgDetailsBinding
import com.ramble.ramblewallet.helper.formatHTML
import com.ramble.ramblewallet.helper.getExtras
import com.ramble.ramblewallet.wight.HtmlWebView

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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_msg_details)
        title = getExtras().getString(ARG_PARAM1, "")
        content = getExtras().getString(ARG_PARAM2, "")
        createTime = getExtras().getString(ARG_PARAM3, "")
        typeText = getExtras().getInt(ARG_PARAM4, 0)
        binding.tvMineTitle.text = when (typeText) {
            1 -> {
                binding.ivRight.visibility=View.GONE
                getString(R.string.help_center_content)
            }
            2 -> {
                binding.ivRight.visibility=View.GONE
                getString(R.string.message_details)
            }
            else -> getString(R.string.message_details)
        }
        binding.title1.text = title
        binding.time2.text =if(createTime.isNotEmpty())"发布日期：$createTime"   else createTime
        isCheckContent(content, title)
        initListener()

    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
    }

    private fun isCheckContent(content: String, title: String) {
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
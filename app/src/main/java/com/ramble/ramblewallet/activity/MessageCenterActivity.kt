package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityMessageCenterBinding
import com.ramble.ramblewallet.fragment.ProclamationFragment
import com.ramble.ramblewallet.fragment.StationFragment
import com.ramble.ramblewallet.helper.replaceFragment

/***
 * 时间　: 2021/12/15 13:42
 * 作者　: potato
 * 描述　:消息中心
 */
class MessageCenterActivity : BaseActivity(), View.OnClickListener {
    private val fragment = ProclamationFragment.newInstance()
    private val fragment2 = StationFragment.newInstance()
    private var flag = -1
    private lateinit var binding: ActivityMessageCenterBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_center)
        initView()
        initListener()

    }


    /**************初始化view**********/
    private fun initView() {
        replaceFragment(fragment, false, R.id.container)
        binding.rgTab.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_left) {
                binding.tvRight.text = "编辑"
                binding.ivMineRight.isVisible = false
                replaceFragment(fragment, false, R.id.container)
            }
            if (checkedId == R.id.rb_right) {
                binding.tvRight.text = "编辑"
                binding.ivMineRight.isVisible = true
                replaceFragment(fragment2, false, R.id.container)
            }

        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.iv_mine_right -> {
                if (binding.tvRight.text == "编辑") {
                    fragment2.passStatus(true)
                    binding.ivRight.setImageResource(R.drawable.ic_confirm)
                    binding.tvRight.text = "取消"
                } else {
                    fragment2.passStatus(false)
                    binding.ivRight.setImageResource(R.drawable.ic_delelet_line)
                    binding.tvRight.text = "编辑"
                }

            }
        }
    }
}
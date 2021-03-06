package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityMessageCenterBinding
import com.ramble.ramblewallet.fragment.ProclamationFragment
import com.ramble.ramblewallet.fragment.StationFragment
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.wight.adapter.FragmentPagerAdapter2

/***
 * 时间　: 2021/12/15 13:42
 * 作者　: potato
 * 描述　:消息中心
 */
class MessageCenterActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMessageCenterBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_center)
        initView()
        initListener()

    }


    /**************初始化view**********/

    private fun initView() {
        binding.pager.adapter = MyAdapter(supportFragmentManager, this)
        binding.layoutTab.setViewPager(binding.pager)
        binding.layoutTab.setOnTabClickListener {
            when (it) {
                0 -> {
                    binding.tvRight.text = "编辑"
                    binding.ivMineRight.isVisible = false

                }
                else -> {
                    binding.tvRight.text = "编辑"
                    binding.ivRight.setImageResource(R.drawable.ic_delelet_line)
                    RxBus.emitEvent(Pie.EVENT_CHECK_MSG, false)
                    binding.ivMineRight.isVisible = true
                }
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
                    RxBus.emitEvent(Pie.EVENT_CHECK_MSG, true)
                    binding.ivRight.setImageResource(R.drawable.ic_confirm)
                    binding.tvRight.text = "取消"
                } else {
                    RxBus.emitEvent(Pie.EVENT_DELETE_MSG, false)
                    binding.ivRight.setImageResource(R.drawable.ic_delelet_line)
                    binding.tvRight.text = "编辑"
                }

            }
        }
    }

    class MyAdapter(fm: FragmentManager, activity: MessageCenterActivity) :
        FragmentPagerAdapter2(fm) {
        private val titles: Array<String> = arrayOf(
            activity.getString(R.string.announcement),
            activity.getString(R.string.message_center_title)
        )
        val fragment by lazy { ProclamationFragment.newInstance() }
        val fragment2 by lazy { StationFragment.newInstance() }

        override fun getItem(position: Int): Fragment {
            return getFragment(position)
        }

        private fun getFragment(position: Int): Fragment {
            return when (position) {
                0 -> fragment
                1 -> fragment2
                else -> fragment
            }
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }

}
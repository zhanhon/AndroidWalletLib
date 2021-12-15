package com.ramble.ramblewallet.pull

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.ramble.ramblewallet.databinding.LayoutRefreshHeaderBinding
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.helper.dataBinding


class PieRefreshHeaderView : LinearLayout, QMUIPullRefreshLayout.IRefreshView {
    lateinit var binding: LayoutRefreshHeaderBinding
        private set
    lateinit var animation: AnimationDrawable
        private set

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    @TargetApi(21)
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    private fun initView(context: Context) {
        this.gravity = Gravity.CENTER
        this.orientation = LinearLayout.VERTICAL
        this.binding = LayoutInflater.from(context).dataBinding(R.layout.layout_refresh_header)
        this.animation = binding.imgAnimation.drawable as AnimationDrawable
        this.addView(binding.root)
        setTime()
    }

    fun setTextColor(color: Int) {
        binding.txtTime.setTextColor(color)
        binding.txtTitle.setTextColor(color)
    }

    override fun stop() {
        animation.stop()
        binding.txtTitle.setText(R.string.refresh_pull_down)
        setTime()
    }

    override fun doRefresh() {
        animation.start()
        binding.txtTitle.setText(R.string.refresh_ing)
    }

    override fun onPull(offset: Int, total: Int, overPull: Int) {
        if (animation.isRunning) return
        if (offset >= total) {
            binding.txtTitle.setText(R.string.refresh_release)
        } else {
            binding.txtTitle.setText(R.string.refresh_pull_down)
        }
    }

    private fun setTime() {
//        val time = FORMATTER_DD_MM_YYYY_HH_MM.format(Instant.now().atZone(ZoneId.systemDefault()))
//        binding.txtTime.text = StringUtils.phrase3(R.string.refresh_time)
//            .with("key", time.substring(time.length - 5, time.length))
//            .build()
        binding.txtTime.isVisible=false
    }
}

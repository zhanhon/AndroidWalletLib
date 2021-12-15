package com.ramble.ramblewallet.wight

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.databinding.LayoutRefreshHeaderBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

/**
 * 时间　: 2021/12/15 15:52
 * 作者　: potato
 * 描述　:
 */
class RWRefreshHeaderView : LinearLayout, RefreshHeader {
    lateinit var binding: LayoutRefreshHeaderBinding
        private set
    lateinit var animation: AnimationDrawable
        private set
    private val FORMATTER_DD_MM_YYYY_HH_MM: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
    private lateinit var mContext: Context//上下文

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
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        animation.stop()
        return 0
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {

    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {

    }

    override fun getView(): View {
        return this
    }

    override fun setPrimaryColors(vararg colors: Int) {
        if (colors.isNotEmpty()) {
            binding.txtTitle.setTextColor(colors[0])
        }
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        animation.start()
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                binding.txtTitle.setText(R.string.refresh_pull_down)
                val time =
                    FORMATTER_DD_MM_YYYY_HH_MM.format(Instant.now().atZone(ZoneId.systemDefault()))
//                binding.txtTime.text = StringUtils.phrase3(R.string.refresh_time)
//                    .with("key", time.substring(time.length - 5, time.length))
//                    .build()
                binding.txtTime.isVisible=false
            }
            RefreshState.Refreshing -> {
                binding.txtTitle.setText(R.string.refresh_ing)
            }
            RefreshState.ReleaseToRefresh -> {
                binding.txtTitle.setText(R.string.refresh_release)
            }
        }
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {

    }

    override fun isSupportHorizontalDrag(): Boolean = false
}
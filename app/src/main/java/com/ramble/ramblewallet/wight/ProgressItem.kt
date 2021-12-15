package com.ramble.ramblewallet.wight

import android.graphics.PorterDuff
import android.widget.ProgressBar
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.helper.toColor
import com.ramble.ramblewallet.wight.adapter.DefaultBinding
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2021/12/15 19:45
 * 作者　: potato
 * 描述　:
 */
object ProgressItem : SimpleRecyclerItem() {
    override fun getLayout(): Int = R.layout.item_progress

    override fun bind(holder: ViewHolder) {
        val binding: DefaultBinding = holder.binding()
        binding.findView<ProgressBar>(R.id.progress).apply {
            indeterminateDrawable.setColorFilter(
                context.toColor(R.color.primary),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    fun removeFrom(adapter: RecyclerAdapter) {
        if (adapter.contains(this)) {
            adapter.remove(this)
        }
    }

    fun addTo(adapter: RecyclerAdapter): Boolean {
        if (!adapter.contains(this)) {
            adapter.add(this)
            return true
        }
        return false
    }

    fun isLoading(adapter: RecyclerAdapter): Boolean = adapter.contains(this)
}
package com.ramble.ramblewallet.wight

import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem

/**
 * 时间　: 2021/12/16 9:46
 * 作者　: potato
 * 描述　:
 */
abstract class CheckableSimpleRecyclerItem : SimpleRecyclerItem() {
    @JvmField
    var isChecked: Boolean = false

    @JvmField
    var isClickable: Boolean = true

    @JvmField
    var isMore: Boolean = true

    @JvmField
    var isVsb: Boolean = true

}
package com.ramble.ramblewallet.custom

import androidx.recyclerview.widget.ItemTouchHelper

class ItemTouchHelperImpl(private val callback: ItemTouchHelperCallback) :
    ItemTouchHelper(callback) {

    fun setDragEnable(enable: Boolean) {
        callback.setDragEnable(enable)
    }

}
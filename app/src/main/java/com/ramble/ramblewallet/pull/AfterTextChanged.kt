package com.ramble.ramblewallet.pull

import android.text.TextWatcher

abstract class AfterTextChanged : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
}
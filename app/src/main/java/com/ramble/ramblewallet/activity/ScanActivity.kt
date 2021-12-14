package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity

class ScanActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
    }
}
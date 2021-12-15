package com.ramble.ramblewallet.pull

import com.google.android.material.tabs.TabLayout

abstract class OnTabSelected : TabLayout.OnTabSelectedListener {
    override fun onTabUnselected(tab: TabLayout.Tab) {}

    override fun onTabReselected(tab: TabLayout.Tab) {}
}

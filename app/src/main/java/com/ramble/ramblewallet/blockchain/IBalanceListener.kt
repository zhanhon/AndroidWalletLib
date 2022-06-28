package com.ramble.ramblewallet.blockchain

import java.math.BigDecimal

//余额监听
interface IBalanceListener {
    fun onBalance(bigDecimal: BigDecimal)
}
package com.ramble.ramblewallet.blockchain

import org.bitcoinj.core.UTXO

interface ITransferListener {
    fun onTransferSuccess(transactionHash: String, utxos: MutableList<UTXO>?)
    fun onTransferFail(errorMessage: String)
}
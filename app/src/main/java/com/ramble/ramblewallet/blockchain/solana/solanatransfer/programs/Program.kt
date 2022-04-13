package com.ramble.ramblewallet.blockchain.solana.solanatransfer.programs

import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.AccountMeta
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionInstruction
import com.ramble.ramblewallet.blockchain.solana.solanatransfer.core.TransactionPublicKey


/**
 * Created by guness on 6.12.2021 23:30
 */
open class Program {
    /**
     * Returns a [TransactionInstruction] built from the specified values.
     * @param programId Solana program we are calling
     * @param keys AccountMeta keys
     * @param data byte array sent to Solana
     * @return [TransactionInstruction] object containing specified values
     */
    fun createTransactionInstruction(
        programId: TransactionPublicKey,
        keys: List<AccountMeta>,
        data: ByteArray
    ): TransactionInstruction {
        return TransactionInstruction(programId, keys, data)
    }
}

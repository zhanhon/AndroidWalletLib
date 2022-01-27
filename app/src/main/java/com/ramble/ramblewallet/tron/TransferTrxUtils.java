package com.ramble.ramblewallet.tron;

import com.ramble.ramblewallet.tronsdk.StringTronUtil;
import com.ramble.ramblewallet.tronsdk.common.crypto.ECKey;
import com.ramble.ramblewallet.tronsdk.common.utils.TransactionUtils;
import com.ramble.ramblewallet.tronsdk.common.utils.abi.CancelException;
import com.ramble.ramblewallet.tronsdk.common.utils.abi.EncodingException;
import com.ramble.ramblewallet.tronsdk.net.CipherException;
import com.ramble.ramblewallet.tronsdk.net.TronAPI;

import org.tron.api.GrpcAPI;
import org.tron.protos.Contract;
import org.tron.protos.Protocol;

import java.io.IOException;
import java.math.BigInteger;

public class TransferTrxUtils {
    public static void transferTrx(String fromAddress, String toAddress, String privateKey, double number) {
        byte[] toRaw = StringTronUtil.decodeFromBase58Check(toAddress);
        // trx
        Contract.TransferContract contract = TronAPI.createTransferContract(toRaw, StringTronUtil.decodeFromBase58Check(fromAddress), (long) (number * 1000000.0d));
        Protocol.Transaction transactionTRX = TronAPI.createTransaction4Transfer(contract);

        //trx10
//        String tokenId = "";
//        GrpcAPI.TransactionExtention transferAssetTransaction = TronAPI.createTransferAssetTransaction(ToRaw, tokenId.getBytes(), StringTronUtil.decodeFromBase58Check(wallet.getAddress()), (long) count);
//        if (transferAssetTransaction.hasResult()) {
//            Protocol.Transaction transactionTRX10 = transferAssetTransaction.getTransaction();
//        }

        //sign
        Protocol.Transaction mTransactionSigned = TransactionUtils.setTimestamp(transactionTRX);
        mTransactionSigned = TransactionUtils.sign(mTransactionSigned, ECKey.fromPrivate(new BigInteger(privateKey)));

        //broadcastTransaction
        boolean sent = TronAPI.broadcastTransaction(mTransactionSigned);

    }

    public static void transferToken(String fromAddress, String toAddress, String contractAddress, String privateKey, String number) {
        //trx20
        String[] parameters = new String[]{contractAddress,
                "transfer(address,uint256)", toAddress, "false", "100000000", "0"};
        GrpcAPI.TransactionExtention transactionExtention = null;
        try {
            transactionExtention = TronAPI.triggerContract(parameters, StringTronUtil.decodeFromBase58Check(fromAddress));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (CancelException e) {
            e.printStackTrace();
        } catch (EncodingException e) {
            e.printStackTrace();
        }
        if (transactionExtention.hasResult()) {
            Protocol.Transaction transactionTRX20 = transactionExtention.getTransaction();
            //sign
            Protocol.Transaction mTransactionSigned = TransactionUtils.setTimestamp(transactionTRX20);
            mTransactionSigned = TransactionUtils.sign(mTransactionSigned, ECKey.fromPrivate(new BigInteger(privateKey)));
            //broadcastTransaction
            boolean sent = TronAPI.broadcastTransaction(mTransactionSigned);
        }
    }
}

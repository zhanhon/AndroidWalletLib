package com.ramble.ramblewallet.tronsdk.trx;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ramble.ramblewallet.tronsdk.trx.exception.ContractExeException;
import com.ramble.ramblewallet.tronsdk.trx.exception.ContractValidateException;

public interface Actuator {

    boolean execute(Object result) throws ContractExeException;

    boolean validate() throws ContractValidateException;

    ByteString getOwnerAddress() throws InvalidProtocolBufferException;

    long calcFee();

}

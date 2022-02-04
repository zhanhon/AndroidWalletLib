package com.ramble.ramblewallet.trx;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ramble.ramblewallet.trx.exception.ContractExeException;
import com.ramble.ramblewallet.trx.exception.ContractValidateException;

public interface Actuator {

    boolean execute(Object result) throws ContractExeException;

    boolean validate() throws ContractValidateException;

    ByteString getOwnerAddress() throws InvalidProtocolBufferException;

    long calcFee();

}

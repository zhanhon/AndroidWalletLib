package com.ramble.ramblewallet.tron.tronsdk.trx;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.tron.TronWalletApi;
import org.tron.protos.Protocol;
import org.tron.wallet.crypto.ECKey;
import org.tron.wallet.util.ByteArray;
import org.tron.wallet.util.Sha256Hash;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TrxApi {

    public TrxApi(String addressNumber, String prikeyHex) {
        this.addressNumber = addressNumber;
        this.prikeyHex = prikeyHex;
    }

    public TrxApi(String addressNumber, String addressT, String prikeyHex) {
        this.addressNumber = addressNumber;
        this.addressT = addressT;
        this.prikeyHex = prikeyHex;
    }

    /**
     * 41 开头的地址
     */
    private final String addressNumber;
    /**
     * T开头地址
     */
    private String addressT;
    /**
     * 秘钥
     */
    private final String prikeyHex;

    /**
     * 41 ---- > T
     *
     * @param address
     * @return
     */
    public static String fromHexAddress(String address) {
        return TronWalletApi.encode58Check(ByteArray.fromHexString(address));
    }

    /**
     * T ---->  41
     *
     * @param address
     * @return
     */
    public static String toHexAddress(String address) {
        return ByteArray.toHexString(TronWalletApi.decodeFromBase58Check(address));
    }

    public static byte[] signTransaction2Byte(byte[] transaction, byte[] privateKey)
            throws InvalidProtocolBufferException {
        ECKey ecKey = ECKey.fromPrivate(privateKey);
        Protocol.Transaction transaction1 = Protocol.Transaction.parseFrom(transaction);
        byte[] rawdata = transaction1.getRawData().toByteArray();
        byte[] hash = Sha256Hash.hash(rawdata);
        byte[] sign = ecKey.sign(hash).toByteArray();
        return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build().toByteArray();
    }

    public static String encoderAbi(String Address, BigInteger amount) {
        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(Address));
        inputParameters.add(new Uint256(amount));
        return FunctionEncoder.encodeConstructor(inputParameters);
    }

}

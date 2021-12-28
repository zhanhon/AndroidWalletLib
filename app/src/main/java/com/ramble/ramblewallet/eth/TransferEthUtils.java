package com.ramble.ramblewallet.eth;

import android.annotation.SuppressLint;
import android.util.Log;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/19
 */
public class TransferEthUtils {


    public static void transferTest() throws Exception {
        String from = "0x5273c93c4aa6f857cd805644339cf8990bc71b50";
        String to = "0xd46e597892f81e9fff5b3caefc476488eda2995f";
        String privateKey = "ea2ed39bd5b4dfec655c77f1b28a6c1f823d4a5868056f61db815d080e94acae";
        double number = 100000000;  //100000000=100ERC
        int gasPrice = 20;
        int gasLimit = 210000;
        String desc = "aaaaaaaaas";
        //transferMain(from, to, privateKey, number, BigInteger.valueOf(gasPrice), BigInteger.valueOf(gasLimit), desc);
        transferToken(from, to, privateKey, BigDecimal.valueOf(number).toBigInteger(), BigInteger.valueOf(gasPrice), BigInteger.valueOf(gasLimit), desc);
    }

    @SuppressLint("LongLogTag")
    public static void transferMain(String fromAddress, String toAddress, String privateKey, String number,
                                    BigInteger gasPrice, BigInteger gasLimit, String desc) throws Exception {
        Web3j web3j = Web3jFactory.build(new HttpService("http://13.229.173.84:8545"));
        BigInteger value = Convert.toWei(number, Convert.Unit.ETHER).toBigInteger();
        //加载转账所需的凭证，用私钥
        Credentials credentials = Credentials.create(privateKey);
        //获取nonce，交易笔数
        EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = transactionCount.getTransactionCount();
        //Log.i(TAG, "transfer nonce : " + nonce);

        //创建RawTransaction交易对象
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, value, desc);
        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signMessage);

        //发送交易
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            Log.v("-=-=-=->Transfer Error:", ethSendTransaction.getError().getMessage());
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            Log.v("-=-=-=->Transfer TransactionHash:", transactionHash);
        }
    }

    @SuppressLint("LongLogTag")
    public static void transferToken(String fromAddress, String toAddress, String privateKey, BigInteger number,
                                     BigInteger gasPrice, BigInteger gasLimit, String desc) throws Exception {
        Web3j web3j = Web3jFactory.build(new HttpService("http://13.229.173.84:8545"));
        //加载转账所需的凭证，用私钥
        Credentials credentials = Credentials.create(privateKey);
        //获取nonce，交易笔数
        EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = transactionCount.getTransactionCount();
        //Log.i(TAG, "transfer nonce : " + nonce);

        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(number)),
                Arrays.asList(new TypeReference<Type>() {
                }));

        String encodedFunction = FunctionEncoder.encode(function);
        //创建RawTransaction交易对象
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice,
                gasLimit, "0x245A86D04C678E1Ab7e5a8FbD5901C12361Ea308", encodedFunction); //USDT合约地址，暂时写死用于测试

        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signMessage);

        //发送交易
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            Log.v("-=-=-=->Transfer Error:", ethSendTransaction.getError().getMessage());
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            Log.v("-=-=-=->Transfer TransactionHash:", transactionHash);
        }
    }


}

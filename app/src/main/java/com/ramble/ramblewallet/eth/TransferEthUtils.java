package com.ramble.ramblewallet.eth;

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

import java.math.BigInteger;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/19
 */
public class TransferEthUtils {
    String from = "0x85a22B7ECC69C6E7E888BD9E9164bD3841e0E21d";
    String to = "0xc706ddcd78ccd994c604770787e71d231cc66fd2";
    String privateKey = "your privatekey";
    String number = "11212.12";

    public static void transfer(String fromAddress, String toAddress, String privateKey, String number,
                                BigInteger gasPrice, BigInteger gasLimit, String desc) throws Exception {
        Web3j web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161"));
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
        String transactionHash = ethSendTransaction.getTransactionHash();
        //Log.i(TAG, "transfer txhash : " + transactionHash);
    }

}

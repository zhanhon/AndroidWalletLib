package com.ramble.ramblewallet.ethereum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ramble.ramblewallet.activity.TransferActivity;
import com.ramble.ramblewallet.ethereum.utils.StringHexUtils;

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
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Future;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/19
 */
public class TransferEthUtils {
    private static final String DATA_PREFIX = "0x70a08231000000000000000000000000";
    private static final String ETH_NODE = "http://122.248.244.178:8545";//测试节点
    //private static final String ETH_NODE = "http://18.141.62.32:8545";//开发节点

    public static void transferTest() throws Exception {
        String from = "0x5273c93c4aa6f857cd805644339cf8990bc71b50";
        String to = "0xd46e597892f81e9fff5b3caefc476488eda2995f";
        String privateKey = "ea2ed39bd5b4dfec655c77f1b28a6c1f823d4a5868056f61db815d080e94acae";
        //String contractAddress = "0x245A86D04C678E1Ab7e5a8FbD5901C12361Ea308";
        String contractAddress = "0x97fd68AaEaaEb64BD3f5D1EDC26dbbc70B548896";
        BigInteger number = new BigInteger("100000000000000000");  //100000000=100ERC
        //BigInteger gasPrice = new BigInteger("200000000000000");
        BigInteger gasPrice = new BigInteger("121");
        BigInteger gasLimit = new BigInteger("210000");
        String desc = "aaaaaaaaas";
        //transferETH(from, to, privateKey, "1", gasPrice, gasLimit, desc);
        //transferToken(from, to, contractAddress, privateKey, number, gasPrice, gasLimit, desc);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static BigDecimal getBalanceETH(String address) throws Exception {
        try {
            Web3j web3 = Web3j.build(new HttpService(ETH_NODE));
            Future<EthGetBalance> ethGetBalanceFuture = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync();
            return Convert.fromWei(ethGetBalanceFuture.get().getBalance().toString(),
                    Convert.Unit.ETHER);
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal(0.00000000);
        }
    }

    public static BigDecimal getBalanceToken(String address, String contractAddress) throws IOException {
        try {
            String value = Web3j.build(new HttpService(ETH_NODE))
                    .ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address,
                            contractAddress, DATA_PREFIX + address.substring(2)), DefaultBlockParameterName.PENDING).send().getValue();
            String s = new BigInteger(value.substring(2), 16).toString();
            if (s == "0x") {
                return new BigDecimal(0.000000);
            } else {
                return new BigDecimal(s).divide(BigDecimal.valueOf(1000000), 6, RoundingMode.UP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal(0.000000);
        }
    }

    @SuppressLint("LongLogTag")
    public static void transferETH(Activity context, String fromAddress, String toAddress, String privateKey, String number,
                                   BigInteger gasPrice, BigInteger gasLimit, String remark) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(ETH_NODE));
        BigInteger value = Convert.toWei(number, Convert.Unit.ETHER).toBigInteger();
        //加载转账所需的凭证，用私钥
        Credentials credentials = Credentials.create(privateKey);
        //获取nonce，交易笔数
        EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = transactionCount.getTransactionCount();
        //Log.i(TAG, "transfer nonce : " + nonce);

        if (remark != null) {
            remark = StringHexUtils.byte2HexString(remark.getBytes(StandardCharsets.UTF_8));
        }
        //创建RawTransaction交易对象
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, value, remark);
        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signMessage);

        //发送交易
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            if (context instanceof TransferActivity) {
                ((TransferActivity) context).transferFail(ethSendTransaction.getError().getMessage());
            }
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            if (context instanceof TransferActivity) {
                ((TransferActivity) context).transferSuccess(transactionHash);
            }
        }
    }

    @SuppressLint("LongLogTag")
    public static void transferETHToken(Context context, String fromAddress, String toAddress, String contractAddress, String privateKey, BigInteger number,
                                        BigInteger gasPrice, BigInteger gasLimit, String remark) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(ETH_NODE));
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
                gasLimit, contractAddress, encodedFunction); //USDT合约地址，暂时写死用于测试

        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signMessage);

        //发送交易
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            if (context instanceof TransferActivity) {
                ((TransferActivity) context).transferFail(ethSendTransaction.getError().getMessage());
            }
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            if (context instanceof TransferActivity) {
                ((TransferActivity) context).transferSuccess(transactionHash);
            }
        }
    }


}

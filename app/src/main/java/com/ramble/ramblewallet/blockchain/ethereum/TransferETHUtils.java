package com.ramble.ramblewallet.blockchain.ethereum;

import static org.web3j.utils.Convert.Unit.WEI;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ramble.ramblewallet.BuildConfig;
import com.ramble.ramblewallet.bean.MainTokenBean;
import com.ramble.ramblewallet.blockchain.ITransferListener;
import com.ramble.ramblewallet.blockchain.ethereum.utils.EthUtils;
import com.ramble.ramblewallet.constant.ConstantsKt;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
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
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/19
 */
public class TransferETHUtils {

//    public static final String APIKEY = "0b81da36bb9b4532bbd865c26e79ac98";
    public static final String APIKEY = "0941bc1785a84adb9d9c943da76ba023";

    private static final String DATA_PREFIX = "0x70a08231000000000000000000000000";
    private static final String node = BuildConfig.RPC_ETH_NODE;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static BigDecimal getBalanceETH(String address) {
        try {
            Web3j web3 = Web3j.build(new HttpService(node + "/" + APIKEY));
            Future<EthGetBalance> ethGetBalanceFuture = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync();
            return new BigDecimal(ethGetBalanceFuture.get().getBalance().toString()).divide(new BigDecimal("10").pow(18));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return BigDecimal.valueOf(0);
        }
    }

    public static BigDecimal getBalanceToken(String fromAddress, String contractAddress) {
        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address address = new Address(fromAddress);
        inputParameters.add(address);

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

        EthCall ethCall;
        BigDecimal balanceValue = BigDecimal.ZERO;
        try {
            Web3j web3j = Web3j.build(new HttpService(node + "/" + APIKEY));
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            int value = 0;
            if(results != null && results.size()>0){
                value = Integer.parseInt(String.valueOf(results.get(0).getValue()));
            }
            balanceValue = new BigDecimal(value).divide(WEI.getWeiFactor(), 6, RoundingMode.HALF_DOWN);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue;
    }

    public static void getBalanceToken(String address, MainTokenBean tokenBean,BalanceGet balanceGet) {
        try {
            String value = Web3j.build(new HttpService(node + "/" + APIKEY))
                    .ethCall(Transaction.createEthCallTransaction(address,
                            tokenBean.getContractAddress(), DATA_PREFIX + address.substring(2)), DefaultBlockParameterName.PENDING).send().getValue();
            String s = new BigInteger(value.substring(2), 16).toString();
            BigDecimal finalBalance;
            if (s.equals("0x")) {
                finalBalance = BigDecimal.valueOf(0);
            } else {
                finalBalance = new BigDecimal(s).divide(new BigDecimal("10").pow(tokenBean.getDecimalPoints()));
            }
            balanceGet.onListener(finalBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    public static void transferETH(String fromAddress, String toAddress, String privateKey, String number,
                                   BigInteger gasPrice, BigInteger gasLimit, String remark, ITransferListener listener) {
        try {
            Web3j web3j = Web3j.build(new HttpService(node + "/" + APIKEY));
            BigInteger value = Convert.toWei(number, Convert.Unit.ETHER).toBigInteger();
            //加载转账所需的凭证，用私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取nonce，交易笔数
            EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = transactionCount.getTransactionCount();

            if (remark != null) {
                remark = EthUtils.byte2HexString(remark.getBytes(StandardCharsets.UTF_8));
            }
            //创建RawTransaction交易对象
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, value, remark);
            //签名Transaction，这里要对交易做签名
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);

            //发送交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            if (ethSendTransaction.hasError()) {
                listener.onTransferFail(ethSendTransaction.getError().getMessage());
            } else {
                String transactionHash = ethSendTransaction.getTransactionHash();
                listener.onTransferSuccess(transactionHash,null);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @SuppressLint("LongLogTag")
    public static void transferETHToken(String fromAddress, String toAddress, String contractAddress, String privateKey, BigInteger number,
                                        BigInteger gasPrice, BigInteger gasLimit,ITransferListener listener) {
        try {
            Web3j web3j = Web3j.build(new HttpService(node + "/" + APIKEY));
            //加载转账所需的凭证，用私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取nonce，交易笔数
            EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = transactionCount.getTransactionCount();

            Function function = new Function(
                    "transfer",
                    Arrays.asList(new Address(toAddress), new Uint256(number)),
                    Arrays.asList(new TypeReference<Type>() {
                    }));

            String encodedFunction = FunctionEncoder.encode(function);
            //创建RawTransaction交易对象
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice,
                    gasLimit, contractAddress, encodedFunction);

            //签名Transaction，这里要对交易做签名
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);

            //发送交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            if (ethSendTransaction.hasError()) {
                listener.onTransferFail(ethSendTransaction.getError().getMessage());
            } else {
                String transactionHash = ethSendTransaction.getTransactionHash();
                listener.onTransferSuccess(transactionHash, null);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }


    public interface BalanceGet {
        void onListener(BigDecimal tokenBalance);
    }

}

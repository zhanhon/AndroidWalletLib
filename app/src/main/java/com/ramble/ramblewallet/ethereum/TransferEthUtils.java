package com.ramble.ramblewallet.ethereum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ramble.ramblewallet.BuildConfig;
import com.ramble.ramblewallet.activity.TransferActivity;
import com.ramble.ramblewallet.bean.MainETHTokenBean;
import com.ramble.ramblewallet.bean.StoreInfo;
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

    public static final String APIKEY = "0b81da36bb9b4532bbd865c26e79ac98";

    private static final String DATA_PREFIX = "0x70a08231000000000000000000000000";

    public static BalanceGet getBalance;

    public interface BalanceGet {
        void onListener(MainETHTokenBean tokenBean, BigDecimal tokenBalance);
    }

    public void setOnListener(BalanceGet getBalance) {
        this.getBalance = getBalance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static BigDecimal getBalanceETH(String address) {
        try {
            Web3j web3 = Web3j.build(new HttpService(BuildConfig.RPC_ETH_NODE[0] + "/" + APIKEY));
            Future<EthGetBalance> ethGetBalanceFuture = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync();
            return Convert.fromWei(ethGetBalanceFuture.get().getBalance().toString(),
                    Convert.Unit.ETHER);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.valueOf(0.00000000);
        }
    }

    public static BigDecimal getBalanceToken(String address, MainETHTokenBean tokenBean) throws IOException {
        try {
            String value = Web3j.build(new HttpService(BuildConfig.RPC_ETH_NODE[0] + "/" + APIKEY))
                    .ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address,
                            tokenBean.getContractAddress(), DATA_PREFIX + address.substring(2)), DefaultBlockParameterName.PENDING).send().getValue();
            String s = new BigInteger(value.substring(2), 16).toString();
            if (s.equals("0x")) {
                return BigDecimal.valueOf(0.000000);
            } else {
                getBalance.onListener(tokenBean, new BigDecimal(s).divide(BigDecimal.valueOf(1000000), 6, RoundingMode.UP));
                return new BigDecimal(s).divide(BigDecimal.valueOf(1000000), 6, RoundingMode.UP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.valueOf(0.000000);
        }
    }

    @SuppressLint("LongLogTag")
    public static void transferETH(Activity context, String fromAddress, String toAddress, String privateKey, String number,
                                   BigInteger gasPrice, BigInteger gasLimit, String remark) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(BuildConfig.RPC_ETH_NODE[0] + "/" + APIKEY));
        BigInteger value = Convert.toWei(number, Convert.Unit.ETHER).toBigInteger();
        //加载转账所需的凭证，用私钥
        Credentials credentials = Credentials.create(privateKey);
        //获取nonce，交易笔数
        EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = transactionCount.getTransactionCount();

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
        Web3j web3j = Web3j.build(new HttpService(BuildConfig.RPC_ETH_NODE[0] + "/" + APIKEY));
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

package com.ramble.ramblewallet.blockchain.dogecoin;

import static com.alibaba.fastjson.JSON.parseArray;

import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ramble.ramblewallet.BuildConfig;
import com.ramble.ramblewallet.blockchain.IBalanceListener;
import com.ramble.ramblewallet.blockchain.ITransferListener;
import com.ramble.ramblewallet.constant.ConstantsKt;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.jetbrains.annotations.NotNull;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransferDOGEUtils {

    private static final boolean ISMAINNET = true;
    private static final String host = "DOGE"; //测试节点："DOGETEST"
    private static final String node = BuildConfig.RPC_BTC_NODE;

    private TransferDOGEUtils() {
        throw new IllegalStateException("TransferBTCUtils");
    }

    //"https://api.blockcypher.com/v1/btc/test3/addrs/" + address + "/balance"   //免费用户：每秒2次限流
    public static void balanceOfDoge(String address, IBalanceListener listener) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = node + "/get_address_balance/" + host + "/" + address; //免费用户：每秒5次限流
        Request request = new Request
                .Builder()
                .url(url)//要访问的链接
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onBalance(new BigDecimal("0"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    org.json.JSONObject json = new org.json.JSONObject(string);
                    String balanceBefore = json.getJSONObject("data").optString("confirmed_balance");
                    listener.onBalance(new BigDecimal(balanceBefore));
                } catch (Exception e) {
                    listener.onBalance(new BigDecimal("0"));
                    e.printStackTrace();
                }
            }
        });
    }

    public static void transferDoge(String fromAddress, String toAddress,
                                    String privateKey, BigDecimal amount, String btcFee, ITransferListener listener) {
        final List<UTXO>[] utxos = new List[]{Lists.newArrayList()};
        String url = node + "/get_tx_unspent/" + host + "/" + fromAddress;
        try {
            Call call = getCallGet(url);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onTransferFail(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String httpGet = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(httpGet);
                    if (StringUtils.equals("fail", jsonObject.getString("status"))) {
                        return;
                    }
                    JSONArray unspentOutputs = jsonObject.getJSONObject("data").getJSONArray("txs");
                    List<Map> outputs = parseArray(unspentOutputs.toJSONString(), Map.class);
                    if (outputs.isEmpty()) {
                        return;
                    }
                    for (int i = 0; i < outputs.size(); i++) {
                        Map outputsMap = outputs.get(i);

                        String txHash = outputsMap.get("txid").toString();
                        String txOutput = outputsMap.get("output_no").toString();
                        String script = outputsMap.get("script_hex").toString();
                        String value = outputsMap.get("value").toString();
                        UTXO utxo = new UTXO(
                                Sha256Hash.wrap(txHash),
                                Long.valueOf(txOutput),
                                Coin.valueOf(new BigDecimal(value).multiply(new BigDecimal("100000000")).intValue()),
                                0,
                                false,
                                new Script(Utils.HEX.decode(script)),
                                fromAddress
                        );
                        utxos[0].add(utxo);
                    }
                    //根据金额降序排序
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        utxos[0] = utxos[0].stream().sorted(Comparator.comparing(UTXO::getValue, Comparator.reverseOrder())).collect(Collectors.toList());
                    }
                    long fee = getFee(btcFee, amount.longValue(), utxos[0]);
                    String toHex = null;
                    try {
                        toHex = sign(fromAddress, toAddress, privateKey, amount.longValue(), fee, utxos[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObjectTransaction = new JSONObject();
                    jsonObjectTransaction.put("tx_hex", toHex);
                    String url = node + "/send_tx/" + host;
                    Call callTransaction = getCall(url, jsonObjectTransaction);
                    callTransaction.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            listener.onTransferFail(e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            try {
                                org.json.JSONObject trans = new org.json.JSONObject(result);
                                String constantResult = trans.getJSONObject("data").optString("txid");
                                listener.onTransferSuccess(constantResult,utxos[0]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private static Call getCallGet(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)//要访问的链接
                .build();
        return okHttpClient.newCall(request);
    }


    @NotNull
    private static Call getCall(String url, JSONObject param) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(param));
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request);
    }

    /**
     * btc交易签名
     *
     * @param fromAddress
     * @param toAddress
     * @param privateKey
     * @param amount
     * @param fee
     * @param utxos
     * @return
     * @throws Exception
     */
    public static String sign(String fromAddress, String toAddress, String privateKey, long amount, long fee, List<UTXO> utxos) {
        Transaction transaction = null;
        try {
            NetworkParameters networkParameters = DogeParams.get();
            transaction = new Transaction(networkParameters);
            //找零地址
            String changeAddress = fromAddress;
            Long utxoAmount = 0L;
            List<UTXO> needUtxos = new ArrayList<>();
            //遍历未花费列表，组装合适的item
            for (UTXO utxo : utxos) {
                if (utxoAmount >= (amount + fee)) {
                    break;
                } else {
                    needUtxos.add(utxo);
                    utxoAmount += utxo.getValue().value;
                }
            }
            //消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
            Long changeAmount = utxoAmount - (amount + fee);
            //输出-转给自己(找零)
            if (changeAmount > 0) {
                transaction.addOutput(Coin.valueOf(changeAmount), Address.fromString(networkParameters, changeAddress));
            }
            transaction.addOutput(Coin.valueOf(amount), Address.fromString(networkParameters, toAddress));
            //输入未消费列表项
            ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(privateKey));
            for (UTXO utxo : needUtxos) {
                TransactionOutPoint outPoint = new TransactionOutPoint(networkParameters, utxo.getIndex(), utxo.getHash());
                Script scriptPubKey = utxo.getScript();
                if (ScriptPattern.isP2WPKH(scriptPubKey)) {
                    TransactionInput input = new TransactionInput(networkParameters, transaction, new byte[0], outPoint);
                    transaction.addInput(input);
                    int inputIndex = transaction.getInputs().size() - 1;
                    Script scriptCode = (new ScriptBuilder()).data(ScriptBuilder.createOutputScript(LegacyAddress.fromKey(networkParameters, ecKey)).getProgram()).build();
                    TransactionSignature signature = transaction.calculateWitnessSignature(inputIndex, ecKey, scriptCode, utxo.getValue(), Transaction.SigHash.ALL, true);
                    input.setScriptSig(ScriptBuilder.createEmpty());
                    input.setWitness(TransactionWitness.redeemP2WPKH(signature, ecKey));
                } else {
                    transaction.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true);
                }
            }
            new Context(networkParameters);
            transaction.getConfidence().setSource(TransactionConfidence.Source.NETWORK);
            transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(org.apache.commons.codec.binary.Hex.encodeHex(transaction.bitcoinSerialize()));
    }

    /**
     * 获取矿工费用
     *
     * @param amount
     * @param utxos
     * @return
     */
    public static Long getFee(String btcFee, long amount, List<UTXO> utxos) {
        //获取费率
        Long feeRate = Long.parseLong(btcFee);
        Long utxoAmount = 0L;
        Long fee = 0L;
        Long utxoSize = 0L;
        for (UTXO us : utxos) {
            utxoSize++;
            utxoAmount += us.getValue().value;
            fee = (utxoSize * 148 + 34 * 2 + 10) * feeRate;
            if (utxoAmount >= (amount + fee)) {
                break;
            }
        }
        return fee;
    }

}

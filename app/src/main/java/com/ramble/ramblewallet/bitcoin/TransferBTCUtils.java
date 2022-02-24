package com.ramble.ramblewallet.bitcoin;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ramble.ramblewallet.activity.MainBTCActivity;
import com.ramble.ramblewallet.activity.TransferActivity;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

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

public class TransferBTCUtils {

    public static boolean isMainNet;

    static {
        isMainNet = false;
    }

    public static void balanceOfBtc(Activity context, String address) throws JSONException { //限流：每秒好像只能20次
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url("https://api.blockcypher.com/v1/btc/test3/addrs/" + address + "/balance")//要访问的链接
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof MainBTCActivity) {
                    ((MainBTCActivity) context).setBtcBalance(new BigDecimal("0"));
                }
                if (context instanceof TransferActivity) {
                    ((TransferActivity) context).setBtcBalance(new BigDecimal("0"));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    org.json.JSONObject json = new org.json.JSONObject(string);
                    String balanceBefore = json.optString("final_balance");
                    if (context instanceof MainBTCActivity) {
                        ((MainBTCActivity) context).setBtcBalance(new BigDecimal(balanceBefore).divide(new BigDecimal("100000000")));
                    }
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).setBtcBalance(new BigDecimal(balanceBefore).divide(new BigDecimal("100000000")));
                    }
                } catch (Exception e) {
                    if (context instanceof MainBTCActivity) {
                        ((MainBTCActivity) context).setBtcBalance(new BigDecimal("0"));
                    }
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).setBtcBalance(new BigDecimal("0"));
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    public static void transferBTC(Activity context, String fromAddress, String toAddress,
                                   String privateKey, BigDecimal amount, String btcFee, String remark) throws JSONException {
        final List<UTXO>[] utxos = new List[]{Lists.newArrayList()};
        final String[] host = {isMainNet ? "BTC" : "BTCTEST"};
        String url = "https://chain.so/api/v2/get_tx_unspent/" + host[0] + "/" + fromAddress;
        try {
            Call call = getCallGet(url);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).transferFail(e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String httpGet = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(httpGet);
                    if (StringUtils.equals("fail", jsonObject.getString("status"))) {
                        return;
                    }
                    JSONArray unspentOutputs = jsonObject.getJSONObject("data").getJSONArray("txs");
                    List<Map> outputs = JSONObject.parseArray(unspentOutputs.toJSONString(), Map.class);
                    if (outputs == null || outputs.size() == 0) {
                        System.out.println("交易异常，余额不足");
                    }
                    for (int i = 0; i < outputs.size(); i++) {
                        Map outputsMap = outputs.get(i);

                        String tx_hash = outputsMap.get("txid").toString();
                        String tx_output_n = outputsMap.get("output_no").toString();
                        String script = outputsMap.get("script_hex").toString();
                        String value = outputsMap.get("value").toString();
                        UTXO utxo = new UTXO(
                                org.bitcoinj.core.Sha256Hash.wrap(tx_hash),
                                Long.valueOf(tx_output_n),
                                Coin.valueOf(new BigDecimal(value).multiply(new BigDecimal("100000000")).intValue()),
                                0,
                                false,
                                new Script(Utils.HEX.decode(script)),
                                fromAddress
                        );
                        utxos[0].add(utxo);
                    }
                    //TODO 根据金额降序排序
                    utxos[0] = utxos[0].stream().sorted(Comparator.comparing(UTXO::getValue, Comparator.reverseOrder())).collect(Collectors.toList());
                    long fee = getFee(btcFee, amount.longValue(), utxos[0]);
                    String toHex = null;
                    try {
                        toHex = sign(fromAddress, toAddress, privateKey, amount.longValue(), fee, utxos[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObjectTransaction = new JSONObject();
                    jsonObjectTransaction.put("tx_hex", toHex);
                    String url = isMainNet ? "https://chain.so/api/v2/send_tx/BTC" : "https://chain.so/api/v2/send_tx/BTCTEST";
                    Call callTransaction = getCall(url, jsonObjectTransaction);
                    callTransaction.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (context instanceof TransferActivity) {
                                ((TransferActivity) context).transferFail(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            try {
                                org.json.JSONObject trans = new org.json.JSONObject(result);
                                String constant_result = trans.getJSONObject("data").optString("txid");
                                if (context instanceof TransferActivity) {
                                    ((TransferActivity) context).transferSuccess(constant_result);
                                }
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
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(param));
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
    public static String sign(String fromAddress, String toAddress, String privateKey, long amount, long fee, List<UTXO> utxos) throws Exception {
        NetworkParameters networkParameters = isMainNet ? MainNetParams.get() : TestNet3Params.get();
        Transaction transaction = new Transaction(networkParameters);
        //找零地址
        String changeAddress = fromAddress;
        Long utxoAmount = 0L;
        List<UTXO> needUtxos = new ArrayList<UTXO>();
        //获取未消费列表
        if (utxos == null || utxos.size() == 0) {
            throw new Exception("未消费列表为空");
        }
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
        //余额判断
        if (changeAmount < 0) {
            throw new Exception("utxo余额不足");
        }
        //输出-转给自己(找零)
        if (changeAmount > 0) {
            transaction.addOutput(Coin.valueOf(changeAmount), org.bitcoinj.core.Address.fromString(networkParameters, changeAddress));
        }
        transaction.addOutput(Coin.valueOf(amount), Address.fromString(networkParameters, toAddress));
        //输入未消费列表项
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameters, privateKey);
        org.bitcoinj.core.ECKey ecKey = dumpedPrivateKey.getKey();
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
        Context context = new Context(networkParameters);
        transaction.getConfidence().setSource(TransactionConfidence.Source.NETWORK);
        transaction.setPurpose(Transaction.Purpose.USER_PAYMENT);
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
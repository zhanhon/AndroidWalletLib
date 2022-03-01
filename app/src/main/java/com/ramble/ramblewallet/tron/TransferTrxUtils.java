package com.ramble.ramblewallet.tron;

import android.app.Activity;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ramble.ramblewallet.BuildConfig;
import com.ramble.ramblewallet.activity.MainTRXActivity;
import com.ramble.ramblewallet.activity.TransferActivity;
import com.ramble.ramblewallet.tronsdk.common.crypto.ECKey;
import com.ramble.ramblewallet.tronsdk.common.utils.ByteArray;
import com.ramble.ramblewallet.tronsdk.common.utils.Sha256Hash;
import com.ramble.ramblewallet.tronsdk.trx.TrxApi;
import com.ramble.ramblewallet.tronsdk.trx.Util;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.tron.TronWalletApi;
import org.tron.protos.Protocol;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransferTrxUtils {

    public static void isAddressActivate(Activity context, String address) throws JSONException {
        String url = BuildConfig.RPC_TRX_NODE[0] + "/wallet/getaccount";
        JSONObject param = new JSONObject();
        param.put("address", toHexAddress(address));
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof TransferActivity) {
                    ((TransferActivity) context).isTrxAddressActivate(true);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long length = response.body().contentLength();
                if (length == 3) { //当body为null，判断此地址为激活
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).isTrxAddressActivate(false);
                    }
                } else {
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).isTrxAddressActivate(true);
                    }
                }
            }
        });
    }

    public static void balanceOfTrx(Activity context, String address) throws JSONException {
        String url = BuildConfig.RPC_TRX_NODE[0] + "/wallet/getaccount";
        JSONObject param = new JSONObject();
        param.put("address", toHexAddress(address));
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof MainTRXActivity) {
                    ((MainTRXActivity) context).setTrxBalance(new BigDecimal("0"));
                }
                if (context instanceof TransferActivity) {
                    ((TransferActivity) context).setTrxBalance(new BigDecimal("0"));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    JSONObject json = new JSONObject(string);
                    String balanceBefore = json.optString("balance");
                    BigDecimal divide = new BigDecimal(balanceBefore).divide(new BigDecimal("1000000"));
                    if (context instanceof MainTRXActivity) {
                        ((MainTRXActivity) context).setTrxBalance(divide);
                    }
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).setTrxBalance(divide);
                    }
                } catch (Exception e) {
                    if (context instanceof MainTRXActivity) {
                        ((MainTRXActivity) context).setTrxBalance(new BigDecimal("0"));
                    }
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).setTrxBalance(new BigDecimal("0"));
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 查询trc20数量
     */
    public static void balanceOfTrc20(Activity context, String address, String contractAddress) throws JSONException {
        String url = BuildConfig.RPC_TRX_NODE[0] + "/wallet/triggersmartcontract";
        JSONObject param = new JSONObject();
        param.put("owner_address", toHexAddress(address));
        param.put("contract_address", toHexAddress(contractAddress));
        param.put("function_selector", "balanceOf(address)");
        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(toHexAddress(address).substring(2)));
        param.put("parameter", FunctionEncoder.encodeConstructor(inputParameters));
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof MainTRXActivity) {
                    ((MainTRXActivity) context).setTokenBalance(new BigDecimal("0"));
                }
                if (context instanceof TransferActivity) {
                    ((TransferActivity) context).setTokenBalance(new BigDecimal("0"));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    JSONObject json = new JSONObject(string);
                    String constant_result = json.optString("constant_result");
                    String constantResult = constant_result.substring(2, constant_result.length() - 2).replaceAll("^(0+)", "");
                    String balanceBefore = (new BigInteger(constantResult, 16)).toString();
                    if (context instanceof MainTRXActivity) {
                        ((MainTRXActivity) context).setTokenBalance(new BigDecimal(balanceBefore).divide(new BigDecimal("1000000")));
                    }
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).setTokenBalance(new BigDecimal(balanceBefore).divide(new BigDecimal("1000000")));
                    }
                } catch (Exception e) {
                    if (context instanceof MainTRXActivity) {
                        ((MainTRXActivity) context).setTokenBalance(new BigDecimal("0"));
                    }
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).setTokenBalance(new BigDecimal("0"));
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    public static void transferTRX(Activity context, String fromAddress, String toAddress,
                                   String privateKey, BigDecimal number, String remark) throws JSONException {
        String url = BuildConfig.RPC_TRX_NODE[0] + "/wallet/createtransaction";
        JSONObject param = new JSONObject();
        param.put("owner_address", toHexAddress(fromAddress));
        param.put("to_address", toHexAddress(toAddress));
        param.put("amount", number);
        if (StringUtils.isNotEmpty(remark)) {
            param.put("data", Hex.toHexString(remark.getBytes()));
        }
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof TransferActivity) {
                    ((TransferActivity) context).transferFail(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String string = response.body().string();
                try {
                    JSONObject trans = new JSONObject(string);

                    org.tron.protos.Protocol.Transaction transaction = Util.packTransaction2(trans.toString());
                    byte[] bytes = new byte[0];
                    try {
                        bytes = signTransaction2Byte(transaction.toByteArray(), ByteArray.fromHexString(privateKey));
                    } catch (InvalidProtocolBufferException e) {
                        Log.e("TRX交易签名出错 errMsg:{}", e.getMessage(), e);
                    }
                    String signTransation = ByteArray.toHexString(bytes);

                    // 广播交易
                    JSONObject jsonObjectGB = new JSONObject();
                    jsonObjectGB.put("transaction", signTransation);
                    Call call2 = getCall(BuildConfig.RPC_TRX_NODE[0] + "/wallet/broadcasthex", jsonObjectGB);
                    call2.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (context instanceof TransferActivity) {
                                ((TransferActivity) context).transferFail(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String string = response.body().string();
                            try {
                                JSONObject trans = new JSONObject(string);
                                String constant_result = trans.optString("txid");
                                if (context instanceof TransferActivity) {
                                    ((TransferActivity) context).transferSuccess(constant_result);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //TRC20
    public static void transferTRXToken(Activity context, String fromAddress, String toAddress,
                                        String contractAddress, String privateKey, BigInteger amount, String remark) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contract_address", toHexAddress(contractAddress));
        jsonObject.put("function_selector", "transfer(address,uint256)");
        String parameter = TrxApi.encoderAbi(TrxApi.toHexAddress(toAddress).substring(2), amount);
        jsonObject.put("parameter", parameter);
        jsonObject.put("owner_address", TrxApi.toHexAddress(fromAddress));
        jsonObject.put("call_value", 0);
        jsonObject.put("fee_limit", "10000000");
        Call call = getCall(BuildConfig.RPC_TRX_NODE[0] + "/wallet/triggersmartcontract", jsonObject);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof TransferActivity) {
                    ((TransferActivity) context).transferFail(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    JSONObject trans = new JSONObject(string);
                    JSONObject tx = trans.getJSONObject("transaction");
                    if (remark != null) {
                        tx.getJSONObject("raw_data").put("data", Hex.toHexString(remark.getBytes()));
                    }
                    org.tron.protos.Protocol.Transaction transaction = Util.packTransaction(tx.toString());
                    byte[] bytes = TrxApi.signTransaction2Byte(transaction.toByteArray(), ByteArray.fromHexString(privateKey));
                    String signTransation = ByteArray.toHexString(bytes);
                    // 广播交易
                    JSONObject jsonObjectGB = new JSONObject();
                    jsonObjectGB.put("transaction", signTransation);
                    Call call2 = getCall(BuildConfig.RPC_TRX_NODE[0] + "/wallet/broadcasthex", jsonObjectGB);
                    call2.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (context instanceof TransferActivity) {
                                ((TransferActivity) context).transferFail(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String string = response.body().string();
                            try {
                                JSONObject trans = new JSONObject(string);
                                String constant_result = trans.optString("txid");
                                if (context instanceof TransferActivity) {
                                    ((TransferActivity) context).transferSuccess(constant_result);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

}

package com.ramble.ramblewallet.tron;

import android.app.Activity;

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

    public static final String APIKEY = "db3a1845-857d-414f-9d50-8207e39a2ec8";
    private static final String SMARTCONTRACT = "/wallet/triggersmartcontract";
    private static final String OWNERADDRESS = "owner_address";
    private static final String CONTRACTADDRESS = "contract_address";
    private static final String FUNCTIONSELECTOR = "function_selector";
    private static final String PARAMETER = "parameter";
    private static final String EXACT = "1000000";
    private static final String TRANSACTION = "transaction";

    private TransferTrxUtils() {
        throw new IllegalStateException("TransferTrxUtils");
    }

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
                String string = response.body().string();
                if (!string.contains("balance")) { //当body为null，判断此地址为激活
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

    public static void isAddressActivateToken(Activity context, String address, String contractAddress) throws JSONException {
        String url = BuildConfig.RPC_TRX_NODE[0] + SMARTCONTRACT;
        JSONObject param = new JSONObject();
        param.put(OWNERADDRESS, toHexAddress(address));
        param.put(CONTRACTADDRESS, toHexAddress(contractAddress));
        param.put(FUNCTIONSELECTOR, "balanceOf(address)");
        List<Type> inputParameters = new ArrayList();
        inputParameters.add(new Address(toHexAddress(address).substring(2)));
        param.put(PARAMETER, FunctionEncoder.encodeConstructor(inputParameters));
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
                try {
                    String string = response.body().string();
                    JSONObject json = new JSONObject(string);
                    String constantResult = json.optString("constant_result");
                    if (constantResult.contains("0000000000000000000000000000000000000000000000000000000000000000")) { //当body为null，判断此地址为激活
                        if (context instanceof TransferActivity) {
                            ((TransferActivity) context).isTrxAddressActivate(false);
                        }
                    } else {
                        if (context instanceof TransferActivity) {
                            ((TransferActivity) context).isTrxAddressActivate(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                    BigDecimal divide = new BigDecimal(balanceBefore).divide(new BigDecimal(EXACT));
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
        String url = BuildConfig.RPC_TRX_NODE[0] + SMARTCONTRACT;
        JSONObject param = new JSONObject();
        param.put(OWNERADDRESS, toHexAddress(address));
        param.put(CONTRACTADDRESS, toHexAddress(contractAddress));
        param.put(FUNCTIONSELECTOR, "balanceOf(address)");
        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(toHexAddress(address).substring(2)));
        param.put(PARAMETER, FunctionEncoder.encodeConstructor(inputParameters));
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
                    String constantResultBefore = json.optString("constant_result");
                    String constantResult = constantResultBefore.substring(2, constantResultBefore.length() - 2).replaceAll("^(0+)", "");
                    String balanceBefore = (new BigInteger(constantResult, 16)).toString();
                    BigDecimal value = new BigDecimal(balanceBefore).divide(new BigDecimal(EXACT));
                    if (context instanceof MainTRXActivity) {
                        ((MainTRXActivity) context).setTokenBalance(value);
                    }
                    if (context instanceof TransferActivity) {
                        ((TransferActivity) context).setTokenBalance(value);
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
        param.put(OWNERADDRESS, toHexAddress(fromAddress));
        param.put("to_address", toHexAddress(toAddress));
        param.put("amount", number);
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
                    if (remark != null) {
                        trans.getJSONObject("raw_data").put("data", Hex.toHexString(remark.getBytes()));
                    }
                    org.tron.protos.Protocol.Transaction transaction = Util.packTransaction2(trans.toString());
                    byte[] bytes = signTransaction2Byte(transaction.toByteArray(), ByteArray.fromHexString(privateKey));
                    String signTransation = ByteArray.toHexString(bytes);

                    // 广播交易
                    JSONObject jsonObjectGB = new JSONObject();
                    jsonObjectGB.put(TRANSACTION, signTransation);
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
                                String constantResult = trans.optString("txid");
                                if (context instanceof TransferActivity) {
                                    ((TransferActivity) context).transferSuccess(constantResult);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //TRC20
    public static void transferTRXToken(Activity context, String fromAddress, String toAddress,
                                        String contractAddress, String privateKey, BigInteger amount, String remark) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CONTRACTADDRESS, toHexAddress(contractAddress));
        jsonObject.put(FUNCTIONSELECTOR, "transfer(address,uint256)");
        String parameter = TrxApi.encoderAbi(TrxApi.toHexAddress(toAddress).substring(2), amount);
        jsonObject.put(PARAMETER, parameter);
        jsonObject.put(OWNERADDRESS, TrxApi.toHexAddress(fromAddress));
        jsonObject.put("call_value", 0);
        jsonObject.put("fee_limit", "10000000");
        Call call = getCall(BuildConfig.RPC_TRX_NODE[0] + SMARTCONTRACT, jsonObject);
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
                    JSONObject tx = trans.getJSONObject(TRANSACTION);
                    if (remark != null) {
                        tx.getJSONObject("raw_data").put("data", Hex.toHexString(remark.getBytes()));
                    }
                    org.tron.protos.Protocol.Transaction transaction = Util.packTransaction(tx.toString());
                    byte[] bytes = TrxApi.signTransaction2Byte(transaction.toByteArray(), ByteArray.fromHexString(privateKey));
                    String signTransation = ByteArray.toHexString(bytes);
                    // 广播交易
                    JSONObject jsonObjectGB = new JSONObject();
                    jsonObjectGB.put(TRANSACTION, signTransation);
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
                                String constantResult = trans.optString("txid");
                                if (context instanceof TransferActivity) {
                                    ((TransferActivity) context).transferSuccess(constantResult);
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
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(param));
        final Request request = new Request.Builder()
                .url(url)
                .header("TRON-PRO-API-KEY", APIKEY)
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

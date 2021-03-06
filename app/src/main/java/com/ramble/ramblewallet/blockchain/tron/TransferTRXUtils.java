package com.ramble.ramblewallet.blockchain.tron;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ramble.ramblewallet.BuildConfig;
import com.ramble.ramblewallet.blockchain.IAddressActivateListener;
import com.ramble.ramblewallet.blockchain.IBalanceListener;
import com.ramble.ramblewallet.blockchain.ITransferListener;
import com.ramble.ramblewallet.blockchain.tron.tronsdk.common.crypto.ECKey;
import com.ramble.ramblewallet.blockchain.tron.tronsdk.common.utils.ByteArray;
import com.ramble.ramblewallet.blockchain.tron.tronsdk.common.utils.Sha256Hash;
import com.ramble.ramblewallet.blockchain.tron.tronsdk.trx.TrxApi;
import com.ramble.ramblewallet.blockchain.tron.tronsdk.trx.Util;
import com.ramble.ramblewallet.constant.ConstantsKt;
import com.ramble.ramblewallet.network.LoggerInterceptor;

import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransferTRXUtils {

    public static final String APIKEY = "db3a1845-857d-414f-9d50-8207e39a2ec8";
    private static final String SMARTCONTRACT = "/wallet/triggersmartcontract";
    private static final String OWNERADDRESS = "owner_address";
    private static final String CONTRACTADDRESS = "contract_address";
    private static final String FUNCTIONSELECTOR = "function_selector";
    private static final String PARAMETER = "parameter";
    private static final String TRANSACTION = "transaction";
    private static final String node = BuildConfig.RPC_TRX_NODE;

    private TransferTRXUtils() {
        throw new IllegalStateException("TransferTrxUtils");
    }

    public static void isAddressActivate(String address, IAddressActivateListener listener) throws JSONException {
        String url = node + "/wallet/getaccount";
        JSONObject param = new JSONObject();
        param.put("address", toHexAddress(address));
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onAddressActivate(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                if (!string.contains("balance")) { //???body???null???????????????????????????
                    listener.onAddressActivate(false);
                } else {
                    listener.onAddressActivate(true);
                }
            }
        });
    }

    public static void isAddressActivateToken(String address, String contractAddress,IAddressActivateListener listener) throws JSONException {
        String url = node + SMARTCONTRACT;
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
                listener.onAddressActivate(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String string = response.body().string();
                    JSONObject json = new JSONObject(string);
                    String constantResult = json.optString("constant_result");
                    if (constantResult.contains("0000000000000000000000000000000000000000000000000000000000000000")) { //???body???null???????????????????????????
                        listener.onAddressActivate(false);
                    } else {
                        listener.onAddressActivate(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void balanceOfTrx(String address, IBalanceListener listener) throws JSONException {
        String url = node + "/wallet/getaccount";
        JSONObject param = new JSONObject();
        param.put("address", toHexAddress(address));
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onBalance(new BigDecimal("0"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    JSONObject json = new JSONObject(string);
                    String balanceBefore = json.optString("balance");
                    BigDecimal divide = new BigDecimal(balanceBefore).divide(new BigDecimal(10).pow(6));
                    listener.onBalance(divide);
                } catch (Exception e) {
                    listener.onBalance(new BigDecimal("0"));
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ??????trc20??????
     */
    public static void balanceOfTrc20(String address, String contractAddress,IBalanceListener listener) throws JSONException {
        String url = node + SMARTCONTRACT;
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
                listener.onBalance(new BigDecimal("0"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    JSONObject json = new JSONObject(string);
                    String constantResultBefore = json.optString("constant_result");
//                    String constantResult = constantResultBefore.replaceAll("^(0+)", "");
                    BigDecimal value = BigDecimal.ZERO;
                    if (!constantResultBefore.isEmpty()){
                        String balanceBefore = (new BigInteger(constantResultBefore, 16)).toString();
                        value = new BigDecimal(balanceBefore).divide(new BigDecimal(10).pow(6));
                    }
                    listener.onBalance(value);
                } catch (Exception e) {
                    listener.onBalance(new BigDecimal("0"));
                    e.printStackTrace();
                }
            }
        });
    }

    public static void transferTRX(String fromAddress, String toAddress,
                                   String privateKey, BigDecimal number, String remark, ITransferListener listener) throws JSONException {
        String url = node + "/wallet/createtransaction";
        JSONObject param = new JSONObject();
        param.put(OWNERADDRESS, toHexAddress(fromAddress));
        param.put("to_address", toHexAddress(toAddress));
        param.put("amount", number);
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onTransferFail(e.getMessage());
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

                    // ????????????
                    JSONObject jsonObjectGB = new JSONObject();
                    jsonObjectGB.put(TRANSACTION, signTransation);
                    Call call2 = getCall(node + "/wallet/broadcasthex", jsonObjectGB);
                    call2.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            listener.onTransferFail(e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String string = response.body().string();
                            try {
                                JSONObject trans = new JSONObject(string);
                                String constantResult = trans.optString("txid");
                                listener.onTransferSuccess(constantResult, null);
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
    public static void transferTRXToken(String fromAddress, String toAddress, String contractAddress,
                                        String privateKey, BigInteger amount, String remark,ITransferListener listener) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CONTRACTADDRESS, toHexAddress(contractAddress));
        jsonObject.put(FUNCTIONSELECTOR, "transfer(address,uint256)");
        String parameter = TrxApi.encoderAbi(TrxApi.toHexAddress(toAddress).substring(2), amount);
        jsonObject.put(PARAMETER, parameter);
        jsonObject.put(OWNERADDRESS, TrxApi.toHexAddress(fromAddress));
        jsonObject.put("call_value", 0);
        jsonObject.put("fee_limit", "10000000");
        Call call = getCall(node + SMARTCONTRACT, jsonObject);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onTransferFail(e.getMessage());
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
                    // ????????????
                    JSONObject jsonObjectGB = new JSONObject();
                    jsonObjectGB.put(TRANSACTION, signTransation);
                    Call call2 = getCall(node + "/wallet/broadcasthex", jsonObjectGB);
                    call2.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            listener.onTransferFail(e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String string = response.body().string();
                            try {
                                JSONObject trans = new JSONObject(string);
                                String constantResult = trans.optString("txid");
                                listener.onTransferSuccess(constantResult, null);
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
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor())
                .build();
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

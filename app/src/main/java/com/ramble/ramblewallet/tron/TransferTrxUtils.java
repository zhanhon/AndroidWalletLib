package com.ramble.ramblewallet.tron;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ramble.ramblewallet.tronsdk.common.crypto.ECKey;
import com.ramble.ramblewallet.tronsdk.common.utils.ByteArray;
import com.ramble.ramblewallet.tronsdk.common.utils.Sha256Hash;
import com.ramble.ramblewallet.trx.Util;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.tron.TronWalletApi;
import org.tron.protos.Protocol;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransferTrxUtils {

    //主网
    private static final String tronUrl = "https://api.nileex.io";

    public static BigDecimal balanceOf(String address) throws JSONException {
        String url = tronUrl + "/wallet/getaccount";
        JSONObject param = new JSONObject();
        param.put("address", toHexAddress(address));
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("-=-=->failure：", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    JSONObject json = new JSONObject(string);
                    String balance = json.optString("balance");
                    Log.v("-=-=->success：余额：", String.valueOf(new BigDecimal(balance).divide(new BigDecimal("1000000"))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

    /**
     * 查询trc20数量
     */
//    public BigDecimal balanceOfTrc20(String address) throws JSONException {
//        String url = tronUrl +"/wallet/triggerconstantcontract";
//        JSONObject param = new JSONObject();
//        param.put("owner_address", TronUtils.toHexAddress(address));
//        param.put("contract_address", TronUtils.toHexAddress(contract));
//        param.put("function_selector", "balanceOf(address)");
//        List<Type> inputParameters = new ArrayList<>();
//        inputParameters.add(new Address(TronUtils.toHexAddress(address).substring(2)));
//        param.put("parameter", FunctionEncoder.encodeConstructor(inputParameters));
//        String result = HttpClientUtils.postJson(url, param.toJSONString());
//        if (StringUtils.isNotEmpty(result)) {
//            JSONObject obj = JSONObject.parseObject(result);
//            JSONArray results = obj.getJSONArray("constant_result");
//            if (results != null && results.size() > 0) {
//                BigInteger amount = new BigInteger(results.getString(0), 16);
//                return new BigDecimal(amount).divide(decimal, 6, RoundingMode.FLOOR);
//            }
//        }
//        return BigDecimal.ZERO;
//    }
    public static void transferTrx(String fromAddress, String toAddress, String privateKey, double number) throws JSONException {
        String url = tronUrl + "/wallet/createtransaction";
        JSONObject param = new JSONObject();
        param.put("owner_address", toHexAddress(fromAddress));
        param.put("to_address", toHexAddress(toAddress));
        param.put("amount", number);
        Call call = getCall(url, param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("-=-=->failure：", e.getMessage());
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
                    Call call2 = getCall(tronUrl + "/wallet/broadcasthex", jsonObjectGB);
                    call2.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.v("-=-=->failure2：", e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.v("-=-=->success2：", "");
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

    public static void transferToken(String fromAddress, String toAddress, String contractAddress, String privateKey, String number) {
//        //trx20
//        String[] parameters = new String[]{contractAddress,
//                "transfer(address,uint256)", toAddress, "false", "100000000", "0"};
//        GrpcAPI.TransactionExtention transactionExtention = null;
//        try {
//            transactionExtention = TronAPI.triggerContract(parameters, decodeFromBase58Check(fromAddress));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CipherException e) {
//            e.printStackTrace();
//        } catch (CancelException e) {
//            e.printStackTrace();
//        } catch (EncodingException e) {
//            e.printStackTrace();
//        }
//        if (transactionExtention.hasResult()) {
//            Protocol.Transaction transactionTRX20 = transactionExtention.getTransaction();
//            //sign
//            Protocol.Transaction mTransactionSigned = TransactionUtils.setTimestamp(transactionTRX20);
//            mTransactionSigned = TransactionUtils.sign(mTransactionSigned, ECKey.fromPrivate(new BigInteger(privateKey)));
//            //broadcastTransaction
//            boolean sent = TronAPI.broadcastTransaction(mTransactionSigned);
//        }
    }
}

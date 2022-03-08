package com.ramble.ramblewallet.tronsdk.trx;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

import org.tron.protos.Contract;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;
import org.tron.wallet.util.JsonFormat;

import java.lang.reflect.Constructor;

import lombok.extern.slf4j.Slf4j;

import static com.alibaba.fastjson.JSON.parseObject;


@Slf4j(topic = "API")
public class Util {

    public static final String VISIBLE = "visible";
    public static final String VALUE = "value";
    public static final String RAW_DATA = "raw_data";
    public static final String CONTRACT = "contract";
    public static final String PARAMETER = "parameter";

    private Util() {
        throw new IllegalStateException("Util");
    }

    public static Transaction packTransaction(String strTransaction) {
        JSONObject jsonTransaction = parseObject(strTransaction);
        JSONObject rawData = jsonTransaction.getJSONObject(RAW_DATA);
        JSONArray contracts = new JSONArray();
        JSONArray rawContractArray = rawData.getJSONArray(CONTRACT);

        for (int i = 0; i < rawContractArray.size(); i++) {
            try {
                JSONObject contract = rawContractArray.getJSONObject(i);
                JSONObject parameter = contract.getJSONObject(PARAMETER);
                String contractType = contract.getString("type");
                Any any = null;
                Class clazz = TransactionFactory.getContract(ContractType.valueOf(contractType));
                if (clazz != null) {
                    Constructor<GeneratedMessageV3> constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    GeneratedMessageV3 generatedMessageV3 = constructor.newInstance();
                    Message.Builder builder = generatedMessageV3.toBuilder();
                    CharSequence charSequence = parameter.getJSONObject(VALUE).toJSONString();
                    JsonFormat.merge(charSequence, builder);
                    any = Any.pack(builder.build());
                }
                if (any != null) {
                    String value = ByteArray.toHexString(any.getValue().toByteArray());
                    parameter.put(VALUE, value);
                    contract.put(PARAMETER, parameter);
                    contracts.add(contract);
                }
            } catch (JsonFormat.ParseException e) {
                Log.d("ParseException: {}", e.getMessage());
            } catch (Exception e) {
                Log.e("", e.toString());
            }
        }
        rawData.put(CONTRACT, contracts);
        jsonTransaction.put(RAW_DATA, rawData);
        Transaction.Builder transactionBuilder = Transaction.newBuilder();
        try {
            CharSequence charSequence = jsonTransaction.toJSONString();

            JsonFormat.merge(charSequence, transactionBuilder);
            return transactionBuilder.build();
        } catch (JsonFormat.ParseException e) {
            Log.d("ParseException: {}", e.getMessage());
            return null;
        }
    }

    public static Transaction packTransaction2(String strTransaction) {
        JSONObject jsonTransaction = parseObject(strTransaction);
        JSONObject rawData = jsonTransaction.getJSONObject(RAW_DATA);
        JSONArray contracts = new JSONArray();
        JSONArray rawContractArray = rawData.getJSONArray(CONTRACT);

        for (int i = 0; i < rawContractArray.size(); i++) {
            try {
                JSONObject contract = rawContractArray.getJSONObject(i);
                JSONObject parameter = contract.getJSONObject(PARAMETER);
                String contractType = contract.getString("type");
                Any any = null;
                switch (contractType) {
                    case "AccountCreateContract":
                        Contract.AccountCreateContract.Builder accountCreateContractBuilder = Contract.AccountCreateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                accountCreateContractBuilder);
                        any = Any.pack(accountCreateContractBuilder.build());
                        break;
                    case "TransferContract":
                        Contract.TransferContract.Builder transferContractBuilder = Contract.TransferContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject(VALUE).toJSONString(), transferContractBuilder);
                        any = Any.pack(transferContractBuilder.build());
                        break;
                    case "TransferAssetContract":
                        Contract.TransferAssetContract.Builder transferAssetContractBuilder = Contract.TransferAssetContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                transferAssetContractBuilder);
                        any = Any.pack(transferAssetContractBuilder.build());
                        break;
                    case "VoteAssetContract":
                        Contract.VoteAssetContract.Builder voteAssetContractBuilder = Contract.VoteAssetContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject(VALUE).toJSONString(), voteAssetContractBuilder);
                        any = Any.pack(voteAssetContractBuilder.build());
                        break;
                    case "VoteWitnessContract":
                        Contract.VoteWitnessContract.Builder voteWitnessContractBuilder = Contract.VoteWitnessContract
                                .newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject(VALUE).toJSONString(), voteWitnessContractBuilder);
                        any = Any.pack(voteWitnessContractBuilder.build());
                        break;
                    case "WitnessCreateContract":
                        Contract.WitnessCreateContract.Builder witnessCreateContractBuilder = Contract.WitnessCreateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                witnessCreateContractBuilder);
                        any = Any.pack(witnessCreateContractBuilder.build());
                        break;
                    case "AssetIssueContract":
                        Contract.AssetIssueContract.Builder assetIssueContractBuilder = Contract.AssetIssueContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject(VALUE).toJSONString(), assetIssueContractBuilder);
                        any = Any.pack(assetIssueContractBuilder.build());
                        break;
                    case "WitnessUpdateContract":
                        Contract.WitnessUpdateContract.Builder witnessUpdateContractBuilder = Contract.WitnessUpdateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                witnessUpdateContractBuilder);
                        any = Any.pack(witnessUpdateContractBuilder.build());
                        break;
                    case "ParticipateAssetIssueContract":
                        Contract.ParticipateAssetIssueContract.Builder participateAssetIssueContractBuilder =
                                Contract.ParticipateAssetIssueContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                participateAssetIssueContractBuilder);
                        any = Any.pack(participateAssetIssueContractBuilder.build());
                        break;
                    case "AccountUpdateContract":
                        Contract.AccountUpdateContract.Builder accountUpdateContractBuilder = Contract.AccountUpdateContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                accountUpdateContractBuilder);
                        any = Any.pack(accountUpdateContractBuilder.build());
                        break;
                    case "FreezeBalanceContract":
                        Contract.FreezeBalanceContract.Builder freezeBalanceContractBuilder = Contract.FreezeBalanceContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                freezeBalanceContractBuilder);
                        any = Any.pack(freezeBalanceContractBuilder.build());
                        break;
                    case "UnfreezeBalanceContract":
                        Contract.UnfreezeBalanceContract.Builder unfreezeBalanceContractBuilder = Contract.UnfreezeBalanceContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                unfreezeBalanceContractBuilder);
                        any = Any.pack(unfreezeBalanceContractBuilder.build());
                        break;
                    case "UnfreezeAssetContract":
                        Contract.UnfreezeAssetContract.Builder unfreezeAssetContractBuilder = Contract.UnfreezeAssetContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                unfreezeAssetContractBuilder);
                        any = Any.pack(unfreezeAssetContractBuilder.build());
                        break;
                    case "WithdrawBalanceContract":
                        Contract.WithdrawBalanceContract.Builder withdrawBalanceContractBuilder = Contract.WithdrawBalanceContract
                                .newBuilder();
                        JsonFormat.merge(parameter.getJSONObject(VALUE).toJSONString(),
                                withdrawBalanceContractBuilder);
                        any = Any.pack(withdrawBalanceContractBuilder.build());
                        break;
                    case "UpdateAssetContract":
                        Contract.UpdateAssetContract.Builder updateAssetContractBuilder = Contract.UpdateAssetContract
                                .newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject(VALUE).toJSONString(), updateAssetContractBuilder);
                        any = Any.pack(updateAssetContractBuilder.build());
                        break;
                    case "SmartContract":
                        Protocol.SmartContract.Builder smartContractBuilder = Protocol.SmartContract.newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject(VALUE).toJSONString(), smartContractBuilder);
                        any = Any.pack(smartContractBuilder.build());
                        break;
                    case "TriggerSmartContract":
                        Contract.TriggerSmartContract.Builder triggerSmartContractBuilder = Contract.TriggerSmartContract
                                .newBuilder();
                        JsonFormat
                                .merge(parameter.getJSONObject(VALUE).toJSONString(),
                                        triggerSmartContractBuilder);
                        any = Any.pack(triggerSmartContractBuilder.build());
                        break;
                    default:
                }
                if (any != null) {
                    String value = ByteArray.toHexString(any.getValue().toByteArray());
                    parameter.put(VALUE, value);
                    contract.put(PARAMETER, parameter);
                    contracts.add(contract);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        rawData.put(CONTRACT, contracts);
        jsonTransaction.put(RAW_DATA, rawData);
        Transaction.Builder transactionBuilder = Transaction.newBuilder();
        try {
            JsonFormat.merge(jsonTransaction.toJSONString(), transactionBuilder);
            return transactionBuilder.build();
        } catch (Exception e) {
            return null;
        }
    }
}

package com.ramble.ramblewallet.tronsdk.trx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class TransactionNew {

    @SerializedName("ret")
    @Expose
    private List<Ret> ret = new ArrayList<Ret>();
    @SerializedName("signature")
    @Expose
    private List<String> signature = new ArrayList<String>();
    @SerializedName("txID")
    @Expose
    private String txID;
    @SerializedName("raw_data")
    @Expose
    private RawData rawData;
    @SerializedName("raw_data_hex")
    @Expose
    private String rawDataHex;

    /**
     * @return The ret
     */
    public List<Ret> getRet() {
        return ret;
    }

    /**
     * @param ret The ret
     */
    public void setRet(List<Ret> ret) {
        this.ret = ret;
    }

    /**
     * @return The signature
     */
    public List<String> getSignature() {
        return signature;
    }

    /**
     * @param signature The signature
     */
    public void setSignature(List<String> signature) {
        this.signature = signature;
    }

    /**
     * @return The txID
     */
    public String getTxID() {
        return txID;
    }

    /**
     * @param txID The txID
     */
    public void setTxID(String txID) {
        this.txID = txID;
    }

    /**
     * @return The rawData
     */
    public RawData getRawData() {
        return rawData;
    }

    /**
     * @param rawData The raw_data
     */
    public void setRawData(RawData rawData) {
        this.rawData = rawData;
    }

    /**
     * @return The rawDataHex
     */
    public String getRawDataHex() {
        return rawDataHex;
    }

    /**
     * @param rawDataHex The raw_data_hex
     */
    public void setRawDataHex(String rawDataHex) {
        this.rawDataHex = rawDataHex;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(ret).append(signature).append(txID).append(rawData).append(rawDataHex).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TransactionNew) == false) {
            return false;
        }
        TransactionNew rhs = ((TransactionNew) other);
        return new EqualsBuilder().append(ret, rhs.ret).append(signature, rhs.signature).append(txID, rhs.txID).append(rawData, rhs.rawData).append(rawDataHex, rhs.rawDataHex).isEquals();
    }

}

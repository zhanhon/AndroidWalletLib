package com.ramble.ramblewallet.tronsdk.trx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Value {

    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("owner_address")
    @Expose
    private String ownerAddress;
    @SerializedName("contract_address")
    @Expose
    private String contractAddress;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    @SerializedName("to_address")
    @Expose
    private String toAddress;

    /**
     * @return The data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return The ownerAddress
     */
    public String getOwnerAddress() {
        return ownerAddress;
    }

    /**
     * @param ownerAddress The owner_address
     */
    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    /**
     * @return The contractAddress
     */
    public String getContractAddress() {
        return contractAddress;
    }

    /**
     * @param contractAddress The contract_address
     */
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(data).append(ownerAddress).append(contractAddress).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Value)) {
            return false;
        }
        Value rhs = ((Value) other);
        return new EqualsBuilder().append(data, rhs.data).append(ownerAddress, rhs.ownerAddress).append(contractAddress, rhs.contractAddress).isEquals();
    }

}

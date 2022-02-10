package com.ramble.ramblewallet.tronsdk.trx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class RawData {

    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("txTrieRoot")
    @Expose
    private String txTrieRoot;
    @SerializedName("witness_address")
    @Expose
    private String witnessAddress;
    @SerializedName("parentHash")
    @Expose
    private String parentHash;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("contract")
    @Expose
    private List<Contract> contract = new ArrayList<Contract>();
    @SerializedName("ref_block_bytes")
    @Expose
    private String refBlockBytes;
    @SerializedName("ref_block_hash")
    @Expose
    private String refBlockHash;
    @SerializedName("expiration")
    @Expose
    private String expiration;
    @SerializedName("fee_limit")
    @Expose
    private String feeLimit;

    public List<Contract> getContract() {
        return contract;
    }

    public void setContract(List<Contract> contract) {
        this.contract = contract;
    }

    public String getRefBlockBytes() {
        return refBlockBytes;
    }

    public void setRefBlockBytes(String refBlockBytes) {
        this.refBlockBytes = refBlockBytes;
    }

    public String getRefBlockHash() {
        return refBlockHash;
    }

    public void setRefBlockHash(String refBlockHash) {
        this.refBlockHash = refBlockHash;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getFeeLimit() {
        return feeLimit;
    }

    public void setFeeLimit(String feeLimit) {
        this.feeLimit = feeLimit;
    }

    /**
     * @return The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * @return The txTrieRoot
     */
    public String getTxTrieRoot() {
        return txTrieRoot;
    }

    /**
     * @param txTrieRoot The txTrieRoot
     */
    public void setTxTrieRoot(String txTrieRoot) {
        this.txTrieRoot = txTrieRoot;
    }

    /**
     * @return The witnessAddress
     */
    public String getWitnessAddress() {
        return witnessAddress;
    }

    /**
     * @param witnessAddress The witness_address
     */
    public void setWitnessAddress(String witnessAddress) {
        this.witnessAddress = witnessAddress;
    }

    /**
     * @return The parentHash
     */
    public String getParentHash() {
        return parentHash;
    }

    /**
     * @param parentHash The parentHash
     */
    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    /**
     * @return The version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version The version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(number).append(txTrieRoot).append(witnessAddress).append(parentHash).append(version).append(timestamp).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RawData) == false) {
            return false;
        }
        RawData rhs = ((RawData) other);
        return new EqualsBuilder().append(number, rhs.number).append(txTrieRoot, rhs.txTrieRoot).append(witnessAddress, rhs.witnessAddress).append(parentHash, rhs.parentHash).append(version, rhs.version).append(timestamp, rhs.timestamp).isEquals();
    }

}

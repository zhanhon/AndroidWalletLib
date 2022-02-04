package com.ramble.ramblewallet.trx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Ret {

    @SerializedName("contractRet")
    @Expose
    private String contractRet;

    /**
     * 
     * @return
     *     The contractRet
     */
    public String getContractRet() {
        return contractRet;
    }

    /**
     * 
     * @param contractRet
     *     The contractRet
     */
    public void setContractRet(String contractRet) {
        this.contractRet = contractRet;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(contractRet).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Ret) == false) {
            return false;
        }
        Ret rhs = ((Ret) other);
        return new EqualsBuilder().append(contractRet, rhs.contractRet).isEquals();
    }

}

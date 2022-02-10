package com.ramble.ramblewallet.tronsdk.trx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Parameter {

    @SerializedName("value")
    @Expose
    private Value value;
    @SerializedName("type_url")
    @Expose
    private String typeUrl;

    /**
     * @return The value
     */
    public Value getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * @return The typeUrl
     */
    public String getTypeUrl() {
        return typeUrl;
    }

    /**
     * @param typeUrl The type_url
     */
    public void setTypeUrl(String typeUrl) {
        this.typeUrl = typeUrl;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(value).append(typeUrl).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Parameter) == false) {
            return false;
        }
        Parameter rhs = ((Parameter) other);
        return new EqualsBuilder().append(value, rhs.value).append(typeUrl, rhs.typeUrl).isEquals();
    }

}

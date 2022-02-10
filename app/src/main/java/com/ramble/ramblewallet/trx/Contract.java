package com.ramble.ramblewallet.trx;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Contract {

    @SerializedName("parameter")
    @Expose
    private Parameter parameter;
    @SerializedName("type")
    @Expose
    private String type;

    /**
     * @return The parameter
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * @param parameter The parameter
     */
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(parameter).append(type).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Contract) == false) {
            return false;
        }
        Contract rhs = ((Contract) other);
        return new EqualsBuilder().append(parameter, rhs.parameter).append(type, rhs.type).isEquals();
    }

}

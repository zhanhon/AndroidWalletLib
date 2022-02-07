package com.ramble.ramblewallet.bean;

import java.math.BigDecimal;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class MainETHTokenBean {

    private String name;
    private BigDecimal balance;
    private String unitPrice;
    private String currencyUnit;

    public MainETHTokenBean(String name, BigDecimal balance, String unitPrice, String currencyUnit) {
        this.name = name;
        this.balance = balance;
        this.unitPrice = unitPrice;
        this.currencyUnit = currencyUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

}
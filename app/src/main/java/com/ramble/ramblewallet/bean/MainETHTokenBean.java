package com.ramble.ramblewallet.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class MainETHTokenBean implements Serializable {

    private String title;
    private String symbol;
    private BigDecimal balance;
    private String unitPrice;
    private String currencyUnit;
    private String contractAddress;
    private boolean isToken;

    public MainETHTokenBean(String title, String symbol, BigDecimal balance, String unitPrice,
                            String currencyUnit, String contractAddress, boolean isToken) {
        this.title = title;
        this.symbol = symbol;
        this.balance = balance;
        this.unitPrice = unitPrice;
        this.currencyUnit = currencyUnit;
        this.contractAddress = contractAddress;
        this.isToken = isToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public boolean isToken() {
        return isToken;
    }

    public void setToken(boolean token) {
        isToken = token;
    }
}
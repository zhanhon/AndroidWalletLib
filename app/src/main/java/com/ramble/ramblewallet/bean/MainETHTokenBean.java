package com.ramble.ramblewallet.bean;

import java.math.BigDecimal;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class MainETHTokenBean {

    private String name;
    private BigDecimal balance;
    private BigDecimal rate;
    private BigDecimal change;

    public MainETHTokenBean(String name, BigDecimal balance, BigDecimal rate, BigDecimal change) {
        this.name = name;
        this.balance = balance;
        this.rate = rate;
        this.change = change;
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

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }
}
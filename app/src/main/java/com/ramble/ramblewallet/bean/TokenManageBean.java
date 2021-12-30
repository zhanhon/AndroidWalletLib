package com.ramble.ramblewallet.bean;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class TokenManageBean {
    private int index;
    private String name;
    private int status;

    public TokenManageBean(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public TokenManageBean(int index, String name, int status) {
        this.index = index;
        this.name = name;
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setAge(int status) {
        this.status = status;
    }
}
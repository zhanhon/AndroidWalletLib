package com.ramble.ramblewallet.bean;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class MyDataBean {
    private int index;
    private String name;
    private String age;

    public MyDataBean(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public MyDataBean(int index, String name, String age) {
        this.index = index;
        this.name = name;
        this.age = age;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
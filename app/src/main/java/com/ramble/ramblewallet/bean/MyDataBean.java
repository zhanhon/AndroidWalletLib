package com.ramble.ramblewallet.bean;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class MyDataBean {
    private String name;
    private String age;
    public MyDataBean(String name, String age) {
        this.name = name;
        this.age = age;
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
package com.ramble.ramblewallet.push;

/**
 * 时间　: 2021/12/15
 * 作者　: potato
 * 描述　: Sample相关的常量定义类
 */
public class PushConstants {

    private PushConstants() {
        throw new IllegalStateException("Pie");
    }

    /**
     * 应用申请的Appkey
     */
    public static final String APP_KEY = "621eea8d3cf78e2eaac25bc8";//测试61cc2cb0cccde77a8a5cc2e7

    /**
     * 应用申请的UmengMessageSecret
     */
    public static final String MESSAGE_SECRET = "6c2214961a6e9c7789e84d4ec9510ad1";//测试50bb050a3c372095dfbb6610b89bc987

    /**
     * 后台加密消息的密码（仅Demo用，请勿将此密码泄漏）
     */
    public static final String APP_MASTER_SECRET = "kkudrhkmgxqijipn5pfu3arumod4qbch";

    /**
     * 渠道名称，修改为您App的发布渠道名称
     */
    public static final String CHANNEL = "Umeng";

}

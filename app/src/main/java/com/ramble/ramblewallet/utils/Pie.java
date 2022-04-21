package com.ramble.ramblewallet.utils;

/**
 * 时间　: 2021/12/22 19:02
 * 作者　: potato
 * 描述　:
 */
public class Pie {
    public static final int EVENT_CHECK_MSG = 2001270;
    /**
     * 地址本扫描二维码
     */
    public static final int EVENT_ADDRESS_BOOK_SCAN = 2001;
    /**
     * 地址本编辑地址
     */
    public static final int EVENT_ADDRESS_BOOK_UPDATA = 2002;
    /**
     * 新增地址本地址
     */
    public static final int EVENT_ADDRESS_BOOK_ADD = 2003;
    /**
     * 新增代币
     */
    public static final int EVENT_ADD_TOKEN = 2004;
    /**
     * 管理代币
     */
    public static final int EVENT_MINUS_TOKEN = 2005;
    /**
     * 删除代币
     */
    public static final int EVENT_DEL_TOKEN = 2006;
    /**
     * 消除消息
     */
    public static final int EVENT_DELETE_MSG = 2008;
    /**
     * 历史筛选
     */
    public static final int EVENT_TRAN_TYPE = 2009;
    /**
     * 转账页面地址扫描二维码
     */
    public static final int EVENT_RESS_TRANS_SCAN = 2010;
    /**
     * 收到消息，刷新接口
     */
    public static final int EVENT_PUSH_MSG = 2011;

    private Pie() {
        throw new IllegalStateException("Pie");
    }
}

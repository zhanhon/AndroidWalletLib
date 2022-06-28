package com.ramble.ramblewallet.blockchain.dogecoin;

import org.bitcoinj.params.AbstractBitcoinNetParams;

/**
 * @创建人： Ricky
 * @创建时间： 2022/4/29
 * @内容：必须重写DogeParams
 */
public class DogeParams extends AbstractBitcoinNetParams {
    public static final String ID_DOGE_MAINNET = "org.dogecoin.production";
    private static DogeParams instance;

    public DogeParams() {
        super();
        p2shHeader = 22;
        dumpedPrivateKeyHeader = 158;
        addressHeader = 30;
        id = ID_DOGE_MAINNET;
    }

    public static synchronized DogeParams get() {
        if (instance == null) {
            instance = new DogeParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return "main";
    }
}
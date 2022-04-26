package com.ramble.ramblewallet.blockchain.dogecoin.dogesdk;

import org.bitcoinj.params.AbstractBitcoinNetParams;

/**
 * @author zhaoda
 * @date 2019/10/18.
 * GitHub：
 * email：
 * description：
 */
public class DogeParams extends AbstractBitcoinNetParams {
    public static final String ID_DOGE_MAINNET = "org.dogecoin.production";
    public DogeParams() {
        super();
        p2shHeader = 22;
        dumpedPrivateKeyHeader = 158;
        addressHeader = 30;
        id = ID_DOGE_MAINNET;
    }

    private static DogeParams instance;
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

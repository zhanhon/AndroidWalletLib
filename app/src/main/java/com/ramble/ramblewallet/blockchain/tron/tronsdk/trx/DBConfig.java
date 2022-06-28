package com.ramble.ramblewallet.blockchain.tron.tronsdk.trx;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;


public class DBConfig {

    @Getter
    @Setter
    public static Set<String> actuatorSet;

    private DBConfig() {
        throw new IllegalStateException("DBConfig");
    }
}

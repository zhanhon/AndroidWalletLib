package com.ramble.ramblewallet.tron.tronsdk.trx;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;


public class DBConfig {

    private DBConfig() {
        throw new IllegalStateException("DBConfig");
    }

    @Getter
    @Setter
    public static Set<String> actuatorSet;
}

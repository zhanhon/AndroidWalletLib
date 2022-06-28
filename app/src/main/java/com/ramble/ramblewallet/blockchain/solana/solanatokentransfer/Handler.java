package com.ramble.ramblewallet.blockchain.solana.solanatokentransfer;

import java.util.AbstractMap;

public interface Handler {
    void handler(AbstractMap<String, Object> map, Callback callback);
}

package com.ramble.ramblewallet.blockchain.dogecoin.dogesdk;

import javax.annotation.Nullable;

public interface EncryptableItem {
    boolean isEncrypted();
    @Nullable
    byte[] getSecretBytes();
}

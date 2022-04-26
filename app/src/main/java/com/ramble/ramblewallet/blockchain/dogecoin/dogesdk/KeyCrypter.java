package com.ramble.ramblewallet.blockchain.dogecoin.dogesdk;

import org.bitcoinj.wallet.Protos.Wallet.EncryptionType;
import org.spongycastle.crypto.params.KeyParameter;
import java.io.Serializable;

public interface KeyCrypter extends Serializable {
    EncryptionType getUnderstoodEncryptionType();
    byte[] decrypt(EncryptedData encryptedBytesToDecode, KeyParameter aesKey) throws KeyCrypterException;
}

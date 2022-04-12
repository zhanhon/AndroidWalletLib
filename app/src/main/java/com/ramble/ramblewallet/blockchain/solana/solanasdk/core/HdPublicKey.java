package com.ramble.ramblewallet.blockchain.solana.solanasdk.core;

/**
 * Defines a key with a given public key
 */
public class HdPublicKey extends HdKey {
    private byte[] publicKey;

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }
}
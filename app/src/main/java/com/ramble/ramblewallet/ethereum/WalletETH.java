package com.ramble.ramblewallet.ethereum;

/**
 * @author Ricky
 */
public class WalletETH {
    private String walletName;
    private String walletPassword;
    private String mnemonic;
    private String address;
    private String privateKey;
    private String publicKey;
    private String keystore;
    private String filename;
    private Integer type; //1:eth 2:比特币 3:trx
    private Boolean isClickDelete;

    public WalletETH(String mnemonic, String address, String privateKey, String publicKey) {
        this.mnemonic = mnemonic;
        this.address = address;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public WalletETH(String mnemonic, String address, String privateKey, String publicKey, String keystore) {
        this(mnemonic, address, privateKey, publicKey);
        this.keystore = keystore;
    }

    public WalletETH(String walletName, String walletPassword, String mnemonic, String address,
                     String privateKey, String publicKey, String keystore, Integer type) {
        this(mnemonic, address, privateKey, publicKey, keystore);
        this.walletName = walletName;
        this.walletPassword = walletPassword;
        this.type = type;
    }

    public WalletETH(String walletName, String walletPassword, String mnemonic, String address,
                     String privateKey, String publicKey, String keystore, Integer type, Boolean isClickDelete) {
        this(walletName, walletPassword, mnemonic, address, privateKey, publicKey, keystore, type);
        this.isClickDelete = isClickDelete;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletPassword() {
        return walletPassword;
    }

    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getClickDelete() {
        return isClickDelete;
    }

    public void setClickDelete(Boolean clickDelete) {
        isClickDelete = clickDelete;
    }

}

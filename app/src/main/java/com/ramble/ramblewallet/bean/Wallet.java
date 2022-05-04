package com.ramble.ramblewallet.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ricky
 */
public class Wallet implements Serializable {
    private int index;
    private String walletName;
    private String walletPassword;
    private String mnemonic;
    private String address;
    private String privateKey;
    private String keystore;
    private String filename;
    private int walletType;  //链类型|1:ETH|2:TRX|3:BTC|4:SOL|5:DOGE|100:BTC、ETH、TRX、SOL、DOGE
    private boolean isClickDelete;
    private boolean isChoose;
    private boolean isBackupAlready;
    private List<String> mnemonicList;

    public Wallet() {

    }

    public Wallet(String mnemonic, String address, String privateKey) {
        this.mnemonic = mnemonic;
        this.address = address;
        this.privateKey = privateKey;
    }

    public Wallet(String mnemonic, String address, String privateKey, String keystore) {
        this(mnemonic, address, privateKey);
        this.keystore = keystore;
    }

    public Wallet(String walletName, String walletPassword, String mnemonic, String address,
                  String privateKey, String keystore, int walletType, List<String> mnemonicList) {
        this(mnemonic, address, privateKey, keystore);
        this.walletName = walletName;
        this.walletPassword = walletPassword;
        this.walletType = walletType;
        this.mnemonicList = mnemonicList;
    }

    public Wallet(String walletName, String walletPassword, String mnemonic, String address,
                  String privateKey, String keystore, int walletType, List<String> mnemonicList, boolean isClickDelete) {
        this(walletName, walletPassword, mnemonic, address, privateKey, keystore, walletType, mnemonicList);
        this.isClickDelete = isClickDelete;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getMnemonicList() {
        return mnemonicList;
    }

    public void setMnemonicList(List<String> mnemonicList) {
        this.mnemonicList = mnemonicList;
    }

    public boolean isBackupAlready() {
        return isBackupAlready;
    }

    public void setBackupAlready(boolean backupAlready) {
        isBackupAlready = backupAlready;
    }

    public boolean isClickDelete() {
        return isClickDelete;
    }

    public void setClickDelete(boolean clickDelete) {
        isClickDelete = clickDelete;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
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

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

}

package com.ramble.ramblewallet.tron;

import com.ramble.ramblewallet.ethereum.WalletETH;
import com.ramble.ramblewallet.tron.bip32.Bip32ECKeyPair;

import org.tron.common.crypto.ECKey;

import static com.ramble.ramblewallet.tron.Wallet.generateBip44KeyPair;

/**
 * @author Angus
 */
public class WalletTRXUtils {


    /**
     * 通过助记词生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param mnemonic
     * @return
     */
    public static WalletETH generateWalletByMnemonic(String walletname, String walletPassword, String mnemonic) {
        byte[] seed = org.web3j.crypto.MnemonicUtils.generateSeed(mnemonic, walletPassword);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, false);
        ECKey mECKey = ECKey.fromPrivate(bip44Keypair.getPrivateKeyBytes33());
        WalletTron walletTron = new WalletTron(mECKey);
        String address = walletTron.getAddress();
        String privateKey = walletTron.getPrivateKey().toString();
        String publicKey = walletTron.getPublicKey().toString();
        //由于波场无法获取到keystore，所以keystore直接传null
        return new WalletETH(walletname, walletPassword, mnemonic, address, privateKey, publicKey, null, 2);
    }

    /**
     * 通过privateKey生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param privateKey
     * @return
     */
//    public static WalletETH generateWalletByPrivateKey(String walletname, String walletPassword, String privateKey) throws CipherException, IOException {
//        byte[] seed = org.web3j.crypto.MnemonicUtils.generateSeed(mnemonic, walletPassword);
//        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
//        Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, false);
//        ECKey mECKey = ECKey.fromPrivate(bip44Keypair.getPrivateKeyBytes33());
//        WalletTron walletTron = new WalletTron(mECKey);
//        String address = walletTron.getAddress();
//        String privateKey = walletTron.getPrivateKey().toString();
//        String publicKey = walletTron.getPublicKey().toString();
//        //由于波场无法获取到keystore，所以keystore直接传null
//        return new WalletETH(walletname, walletPassword, mnemonic, address, privateKey, publicKey, null, 2);
//    }

    /**
     * 通过keystore生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param keystore
     * @return
     */
//    public static WalletETH generateWalletByKeyStore(String walletname, String walletPassword, String keystore) throws CipherException, IOException {
//        byte[] seed = org.web3j.crypto.MnemonicUtils.generateSeed(mnemonic, walletPassword);
//        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
//        Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, false);
//        ECKey mECKey = ECKey.fromPrivate(bip44Keypair.getPrivateKeyBytes33());
//        WalletTron walletTron = new WalletTron(mECKey);
//        String address = walletTron.getAddress();
//        String privateKey = walletTron.getPrivateKey().toString();
//        String publicKey = walletTron.getPublicKey().toString();
//        //由于波场无法获取到keystore，所以keystore直接传null
//        return new WalletETH(walletname, walletPassword, mnemonic, address, privateKey, publicKey, null, 2);
//    }


}

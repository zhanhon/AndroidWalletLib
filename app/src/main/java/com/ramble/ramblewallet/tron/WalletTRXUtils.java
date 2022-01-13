package com.ramble.ramblewallet.tron;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramble.ramblewallet.ethereum.WalletETH;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.tron.TronWalletApi;
import org.tron.common.utils.ByteArray;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;

import java.util.List;

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
    public static WalletETH generateWalletByMnemonic(String walletname, String walletPassword, String mnemonic) throws Exception {
        DeterministicKey deterministicKey = generateKeyFromMnemonicAndUid(mnemonic, 1);
        ECKeyPair ecKeyPair = ECKeyPair.create(deterministicKey.getPrivKey());

        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        WalletFile walletFile = org.web3j.crypto.Wallet.createLight(walletPassword, ecKeyPair);
        String keystore = objectMapper.writeValueAsString(walletFile);
        WalletFile walletFile2 = objectMapper.readValue(keystore, WalletFile.class);
        ECKeyPair ecKeyPair1 = org.web3j.crypto.Wallet.decrypt(walletPassword, walletFile2);
        String address = fromHexAddress("41" + walletFile.getAddress());
        String privateKey = ecKeyPair1.getPrivateKey().toString(16);
        String publicKey = ecKeyPair1.getPublicKey().toString(16);

        Log.v("-=-=->address：", address);
        Log.v("-=-=->privateKey：", privateKey);
        Log.v("-=-=->keystore：", keystore);

        boolean flag = isTrxValidAddress(address);
        System.out.println("地址合法性为:" + flag);
        return new WalletETH(walletname, walletPassword, mnemonic, address, privateKey, publicKey, null, 2);
    }
//    public static WalletETH generateWalletByMnemonic(String walletname, String walletPassword, String mnemonic) {
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

//    /**
//     * 通过privateKey生成钱包
//     *
//     * @param walletname
//     * @param walletPassword
//     * @param privateKey
//     * @return
//     */
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

//    /**
//     * 通过keystore生成钱包
//     *
//     * @param walletname
//     * @param walletPassword
//     * @param keystore
//     * @return
//     */
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

    public static String fromHexAddress(String address) {
        return TronWalletApi.encode58Check(ByteArray.fromHexString(address));
    }

    /**
     * 校验是否是波场
     *
     * @param input
     * @return
     */
    public static boolean isTrxValidAddress(String input) {
        if (input.isEmpty() || !input.startsWith("T")) {
            return false;
        }
        return input.length() == 34;
    }

    /**
     * 通过助记词和id生成对应的子账户
     *
     * @param mnemonic 助记词
     * @param id       派生子id
     * @return 子账户key
     */
    private static DeterministicKey generateKeyFromMnemonicAndUid(String mnemonic, int id) {
        //为了和其他钱包兼容，设置为空
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");

        DeterministicKey rootKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootKey);
        List<ChildNumber> parentPath = HDUtils.parsePath("M/44H/195H/0H/0");

        return hierarchy.deriveChild(parentPath, true, true, new ChildNumber(id, false));
    }

}

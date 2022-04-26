package com.ramble.ramblewallet.blockchain.dogecoin;

import android.util.Log;

import com.ramble.ramblewallet.bean.Wallet;
import com.ramble.ramblewallet.blockchain.dogecoin.dogesdk.DogeParams;
import com.ramble.ramblewallet.blockchain.dogecoin.dogesdk.ECKey;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

import java.util.List;

/**
 * @author Angus
 */
public class WalletDOGEUtils {

    private WalletDOGEUtils() {
        throw new IllegalStateException("WalletDOGEUtils");
    }

    public static Wallet generateWalletByMnemonic() {
        try {
            String passphrase = "olympic derive maid nature fatigue design pull claim viable run hamster cousin";
            DeterministicKey deterministicKey = generateKeyFromMnemonicAndUid(passphrase, 0);
            ECKeyPair keyPair = ECKeyPair.create(deterministicKey.getPrivKey());
            ECKey ecKey = ECKey.fromPrivate(keyPair.getPrivateKey());
            String address = ecKey.toAddress(DogeParams.get()).toBase58();
            String privateKey = ecKey.getPrivateKeyAsWiF(DogeParams.get());
            Log.v("-=-=->address:", address);
            Log.v("-=-=->privateKey:", privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Wallet("", "", "", "", "", "", 2, null);
    }


    /**
     * 通过privateKey生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param privateKey
     * @return
     */
//    public static Wallet generateWalletByPrivateKey(String walletname, String walletPassword, String privateKey, List<String> mnemonicList) {
//        try {
//            BigInteger pk = Numeric.toBigIntNoPrefix(privateKey);
//            byte[] privateKeyByte = pk.toByteArray();
//            ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyByte);
//
//            //由于通过privateKey无法生成助记词，故恢复钱包助记词可为空，备份时不需要有助记词备份
//            return new Wallet(walletname, walletPassword, null, address, privateKey, keystore, 2, mnemonicList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Wallet("", "", "", "", "", "", 2, null);
//        }
//    }

    /**
     * 校验是否是狗狗币
     *
     * @param input
     * @return
     */
    public static boolean isDogeValidAddress(String input) {
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
        List<ChildNumber> parentPath = HDUtils.parsePath("M/44H/3H/0H/0");
        return hierarchy.deriveChild(parentPath, true, true, new ChildNumber(id, false));
    }

}

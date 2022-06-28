package com.ramble.ramblewallet.blockchain.dogecoin;

import com.ramble.ramblewallet.bean.Wallet;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

import java.util.List;

/**
 * @创建人： Ricky
 * @创建时间： 2022/4/29
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
            LegacyAddress address = LegacyAddress.fromKey(DogeParams.get(), ecKey);
            String privateKey = ecKey.getPrivateKeyAsHex();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Wallet("", "", "", "", "", "", 2, null);
    }

    public static Wallet generateWalletByPrivateKey() {
        try {
            String privateKey = "ad521fa141e9068a9143d463ab38a596961d56aa9e78ff88a87b34d28d728d85";
            ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(privateKey));
            LegacyAddress address = LegacyAddress.fromKey(DogeParams.get(), ecKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Wallet("", "", "", "", "", "", 2, null);

    }

    /**
     * 校验是否是狗狗币
     *
     * @param input
     * @return
     */
    public static boolean isDogeValidAddress(String input) {
        if (input.isEmpty() || !input.startsWith("D")) {
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

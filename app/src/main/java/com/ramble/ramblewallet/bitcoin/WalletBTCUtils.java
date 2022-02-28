package com.ramble.ramblewallet.bitcoin;

import android.util.Log;

import com.ramble.ramblewallet.bean.Wallet;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @创建人： Ricky
 * @创建时间： 2022/2/14
 */
public class WalletBTCUtils {

    public static boolean isMainNet;

    static {
        isMainNet = false;
    }

    /**
     * 通过助记词生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param mnemonic
     * @return
     */
    public static Wallet generateWalletByMnemonic(String walletname, String walletPassword, String mnemonic, ArrayList<String> mnemonicList) {
        try {
            DeterministicKey deterministicKey = generateKeyFromMnemonicAndUid(mnemonic, 0);
            ECKey ecKeyPair = ECKey.fromPrivate(deterministicKey.getPrivKey());
            //主网
            //NetworkParameters networkParameters = MainNetParams.get();
            NetworkParameters networkParameters = TestNet3Params.get();

            String publicKey = Numeric.toHexStringNoPrefixZeroPadded(new BigInteger(ecKeyPair.getPubKey()), 66);
            String privateKey = ecKeyPair.getPrivateKeyEncoded(networkParameters).toString();//ecKeyPair.getPrivateKeyAsHex();

            //1开头的地址
            //LegacyAddress address1 = LegacyAddress.fromKey(networkParameters, ecKeyPair);
            //3开头的地址
            //LegacyAddress address3 = LegacyAddress.fromScriptHash(networkParameters, ecKeyPair.getPubKeyHash());
            //bc1开头的地址
            SegwitAddress segwitAddress = SegwitAddress.fromKey(networkParameters, ecKeyPair);
            Log.v("--->地址；", segwitAddress + "");
            Log.v("--->私钥；", privateKey + "");
            //BTC无keystore
            return new Wallet(walletname, walletPassword, mnemonic, segwitAddress.toBech32(), privateKey, publicKey, "", 3, mnemonicList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Wallet("", "", "", "", "", "", "", 3, null);
        }
    }

    /**
     * 通过privateKey生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param privateKey
     * @return
     */
    public static Wallet generateWalletByPrivateKey(String walletname, String walletPassword, String privateKey, ArrayList<String> mnemonicList) {
        try {
            ECKey ecKeyPair = ECKey.fromPrivate(Numeric.toBigInt(privateKey));
            //主网
            //NetworkParameters networkParameters = MainNetParams.get();
            NetworkParameters networkParameters = TestNet3Params.get();

            String publicKey = Numeric.toHexStringNoPrefixZeroPadded(new BigInteger(ecKeyPair.getPubKey()), 66);

            //1开头的地址
            //LegacyAddress address1 = LegacyAddress.fromKey(networkParameters, ecKeyPair);
            //3开头的地址
            //LegacyAddress address3 = LegacyAddress.fromScriptHash(networkParameters, ecKeyPair.getPubKeyHash());
            //bc1开头的地址
            SegwitAddress segwitAddress = SegwitAddress.fromKey(networkParameters, ecKeyPair);
            //BTC无keystore，由于通过privateKey无法生成助记词，故恢复钱包助记词可为空，备份时不需要有助记词备份
            return new Wallet(walletname, walletPassword, "", segwitAddress.toBech32(), privateKey, publicKey, "", 3, mnemonicList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Wallet("", "", "", "", "", "", "", 3, null);
        }
    }

    /**
     * 通过助记词和id生成对应的子账户
     * <p>
     * M/44H/0H/0H/0  主网地址
     * M/44H/1H/0H/0  测试网地址
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
        List<ChildNumber> parentPath = HDUtils.parsePath("M/84H/1H/0H/0");
        return hierarchy.deriveChild(parentPath, true, true, new ChildNumber(id, false));
    }

    public static boolean isBtcValidAddress(String address) {
        try {
            NetworkParameters networkParameters = null;
            if (isMainNet)
                networkParameters = MainNetParams.get();
            else
                networkParameters = TestNet3Params.get();
            Address address1 = Address.fromString(networkParameters, address);
            return address1 != null;
        } catch (Exception e) {
            return false;
        }
    }
}

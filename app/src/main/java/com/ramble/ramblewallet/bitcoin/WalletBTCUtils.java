package com.ramble.ramblewallet.bitcoin;

import android.util.Log;

import com.ramble.ramblewallet.ethereum.WalletETH;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.MainNetParams;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * @创建人： Ricky
 * @创建时间： 2022/2/14
 */
public class WalletBTCUtils {

    public static WalletETH generateWalletByMnemonic(String walletname, String walletPassword, String mnemonic) {
        try {
            DeterministicKey deterministicKey = generateKeyFromMnemonicAndUid(mnemonic, 0);
            ECKey ecKeyPair = ECKey.fromPrivate(deterministicKey.getPrivKey());
            //主网
            NetworkParameters networkParameters = MainNetParams.get();
            //NetworkParameters networkParameters  = TestNet3Params.get();

            String publicKey = Numeric.toHexStringNoPrefixZeroPadded(new BigInteger(ecKeyPair.getPubKey()), 66);
            String privateKey = ecKeyPair.getPrivateKeyAsHex();

            //1开头的地址
            //LegacyAddress address1 = LegacyAddress.fromKey(networkParameters, ecKeyPair);
            //3开头的地址
            //LegacyAddress address3 = LegacyAddress.fromScriptHash(networkParameters, ecKeyPair.getPubKeyHash());
            //bc1开头的地址
            SegwitAddress segwitAddress = SegwitAddress.fromKey(networkParameters, ecKeyPair);
            Log.v("--->地址；", segwitAddress + "");
            Log.v("--->私钥；", privateKey + "");
            //BTC无keystore
            return new WalletETH(walletname, walletPassword, mnemonic, segwitAddress.toBech32(), privateKey, publicKey, "", 2);
        } catch (Exception e) {
            e.printStackTrace();
            return new WalletETH("", "", "", "", "", "", "", 2);
        }
    }

    public static WalletETH generateWalletByPrivateKey(String walletname, String walletPassword, String privateKey) {
        try {
            ECKey ecKeyPair = ECKey.fromPrivate(Numeric.toBigInt(privateKey));
            //主网
            NetworkParameters networkParameters = MainNetParams.get();
            //NetworkParameters networkParameters  = TestNet3Params.get();

            String publicKey = Numeric.toHexStringNoPrefixZeroPadded(new BigInteger(ecKeyPair.getPubKey()), 66);

            //1开头的地址
            //LegacyAddress address1 = LegacyAddress.fromKey(networkParameters, ecKeyPair);
            //3开头的地址
            //LegacyAddress address3 = LegacyAddress.fromScriptHash(networkParameters, ecKeyPair.getPubKeyHash());
            //bc1开头的地址
            SegwitAddress segwitAddress = SegwitAddress.fromKey(networkParameters, ecKeyPair);
            //BTC无keystore，由于通过privateKey无法生成助记词，故恢复钱包助记词可为空，备份时不需要有助记词备份
            return new WalletETH(walletname, walletPassword, "", segwitAddress.toBech32(), privateKey, publicKey, "", 2);
        } catch (Exception e) {
            Log.v("-=-=-=->e；", e.getMessage());
            e.printStackTrace();
            return new WalletETH("", "", "", "", "", "", "", 2);
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
        List<ChildNumber> parentPath = HDUtils.parsePath("M/84H/0H/0H/0");
        return hierarchy.deriveChild(parentPath, true, true, new ChildNumber(id, false));
    }

    /**
     * btc(bch,usdt)地址是否有效
     * <p>
     * return: true有效,false无效
     */
    public static boolean bitCoinAddressValidate(String addr) {
        if (addr.length() < 26 || addr.length() > 35) {
            return false;
        }

        byte[] decoded = decodeBase58To25Bytes(addr);
        if (decoded == null) {
            return false;
        }

        byte[] hash1 = sha256(Arrays.copyOfRange(decoded, 0, 21));
        byte[] hash2 = sha256(hash1);

        return Arrays.equals(Arrays.copyOfRange(hash2, 0, 4), Arrays.copyOfRange(decoded, 21, 25));
    }


    private static byte[] decodeBase58To25Bytes(String input) {
        BigInteger num = BigInteger.ZERO;
        for (char t : input.toCharArray()) {
            int p = input.indexOf(t);
            if (p == -1) {
                return null;
            }
            num = num.multiply(BigInteger.valueOf(58)).add(BigInteger.valueOf(p));
        }

        byte[] result = new byte[25];
        byte[] numBytes = num.toByteArray();
        System.arraycopy(numBytes, 0, result, result.length - numBytes.length, numBytes.length);
        return result;
    }

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

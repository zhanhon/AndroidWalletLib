package com.ramble.ramblewallet.bitcoin;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @创建人： Ricky
 * @创建时间： 2022/2/14
 */
public class WalletBTCUtils {

//    private fun createBTCWalletFromWords(words: String): TianWallet {
//        //把助记词切割成数组
//        val wordsList = Arrays.asList(*words.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
//        val deterministicSeed = DeterministicSeed(wordsList, null, "", 0)
//        val deterministicKeyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build()
//        //这里运用了BIP44里面提到的算法, 44'是固定的, 后面的一个0'代表的是币种BTC
//        var privKeyBTC = deterministicKeyChain.getKeyByPath(parsePath("m/44'/0'/0'/0/0"), true).privKey
//        //如果是调试模式, 第二个字符串应该填1'
//        if (BuildConfig.DEBUG) {
//            privKeyBTC = deterministicKeyChain.getKeyByPath(parsePath("m/44'/1'/0'/0/0"), true).privKey
//        }
//
//        val ecKey = ECKey.fromPrivate(privKeyBTC)
//        val publickey = Numeric.toHexStringNoPrefixZeroPadded(BigInteger(ecKey.pubKey), 66)
//        //正式环境应该是主网参数
//        var privateKey = ecKey.getPrivateKeyEncoded(MainNetParams.get()).toString()
//        //如果是测试环境, 应该调用测试网参数
//        if (BuildConfig.DEBUG) {
//            privateKey = ecKey.getPrivateKeyEncoded(TestNet3Params.get()).toString()
//            return TianWallet(ecKey.toAddress(TestNet3Params.get()).toString(), publickey, privateKey, words)
//        }
//        return TianWallet(ecKey.toAddress(MainNetParams.get()).toString(), publickey, privateKey, words)
//    }

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

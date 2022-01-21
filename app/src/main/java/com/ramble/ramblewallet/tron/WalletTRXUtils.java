package com.ramble.ramblewallet.tron;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramble.ramblewallet.ethereum.WalletETH;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
//import org.tron.TronWalletApi;
//import org.tron.wallet.util.ByteArray;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

import core.common.StringTronUtil;
import core.common.common.utils.ByteArray;

import static org.web3j.crypto.Wallet.createLight;
import static org.web3j.crypto.Wallet.decrypt;

/**
 * @author Angus
 */
public class WalletTRXUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        WalletFile walletFile = createLight(walletPassword, ecKeyPair);
        String keystore = objectMapper.writeValueAsString(walletFile);
        WalletFile walletFile2 = objectMapper.readValue(keystore, WalletFile.class);
        ECKeyPair ecKeyPair1 = decrypt(walletPassword, walletFile2);
        String address = fromHexAddress("41" + walletFile.getAddress());
        String privateKey = ecKeyPair1.getPrivateKey().toString(16);
        String publicKey = ecKeyPair1.getPublicKey().toString(16);
        return new WalletETH(walletname, walletPassword, mnemonic, address, privateKey, publicKey, keystore, 2);
    }

    /**
     * 通过privateKey生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param privateKey
     * @return
     */
    public static WalletETH generateWalletByPrivateKey(String walletname, String walletPassword, String privateKey) throws Exception {
        BigInteger pk = Numeric.toBigIntNoPrefix(privateKey);
        byte[] privateKeyByte = pk.toByteArray();
        ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyByte);
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        WalletFile walletFile = createLight(walletPassword, ecKeyPair);
        String keystore = objectMapper.writeValueAsString(walletFile);
        WalletFile walletFile2 = objectMapper.readValue(keystore, WalletFile.class);
        ECKeyPair ecKeyPair1 = decrypt(walletPassword, walletFile2);
        String address = fromHexAddress("41" + walletFile.getAddress());
        String publicKey = ecKeyPair1.getPublicKey().toString(16);
        //由于通过privateKey无法生成助记词，故恢复钱包助记词可为空，备份时不需要有助记词备份
        return new WalletETH(walletname, walletPassword, null, address, privateKey, publicKey, keystore, 2);
    }

    /**
     * 通过keystore生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param keystore
     * @return
     */
    public static WalletETH generateWalletByKeyStore(String walletname, String walletPassword, String keystore) throws Exception {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        WalletFile walletFile2 = objectMapper.readValue(keystore, WalletFile.class);
        ECKeyPair ecKeyPair = decrypt(walletPassword, walletFile2);
        WalletFile walletFile1 = createLight(walletPassword, ecKeyPair);
        ECKeyPair ecKeyPair1 = decrypt(walletPassword, walletFile2);
        String address = fromHexAddress("41" + walletFile1.getAddress());
        String privateKey = ecKeyPair1.getPrivateKey().toString(16);
        String publicKey = ecKeyPair1.getPublicKey().toString(16);
        //由于通过keystore无法生成助记词，故恢复钱包助记词可为空，备份时不需要有助记词备份
        return new WalletETH(walletname, walletPassword, null, address, privateKey, publicKey, keystore, 2);
    }

    public static String fromHexAddress(String address) {
        return StringTronUtil.encode58Check(ByteArray.fromHexString(address));
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

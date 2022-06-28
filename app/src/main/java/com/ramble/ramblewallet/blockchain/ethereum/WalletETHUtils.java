package com.ramble.ramblewallet.blockchain.ethereum;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;

import com.develop.mnemonic.KeyPairUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramble.ramblewallet.bean.Wallet;
import com.ramble.ramblewallet.blockchain.ethereum.utils.EthUtils;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * @author Angus
 */
public class WalletETHUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private WalletETHUtils() {
        throw new IllegalStateException("WalletETHUtils");
    }

    /**
     * 通过助记词生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param mnemonic
     * @return
     */
    public static Wallet generateWalletByMnemonic(String walletname, String walletPassword, String mnemonic, List<String> mnemonicList) throws CipherException, IOException {
        try {
            ECKeyPair keyPair = WalletETHUtils.generateBip32ECKeyPair(mnemonic);
            WalletFile walletFile = createWalletFile(walletPassword, keyPair, false);
            String address = EthUtils.getAddress(keyPair);
            String privateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
            String keyStore = objectMapper.writeValueAsString(walletFile);
            //链类型|1:ETH|2:TRX|3:BTC
            return new Wallet(walletname, walletPassword, mnemonic, address, privateKey, keyStore, 1, mnemonicList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Wallet("", "", "", "", "", "", 1, null);
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
    public static Wallet generateWalletByPrivateKey(String walletname, String walletPassword, String privateKey, List<String> mnemonicList) {
        try {
            BigInteger pk = new BigInteger(privateKey, 16);
            ECKeyPair keyPair = ECKeyPair.create(pk);
            WalletFile walletFile = org.web3j.crypto.Wallet.createLight(walletPassword, keyPair);
            String address = EthUtils.getAddress(keyPair);
            String keyStore = objectMapper.writeValueAsString(walletFile);
            //由于通过privateKey无法生成助记词，故恢复钱包助记词可为空，备份时不需要有助记词备份
            return new Wallet(walletname, walletPassword, null, address, privateKey, keyStore, 1, mnemonicList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Wallet("", "", "", "", "", "", 1, null);
        }
    }

    /**
     * 通过keystore生成钱包
     *
     * @param walletname
     * @param walletPassword
     * @param keystore
     * @return
     */
    public static Wallet generateWalletByKeyStore(String walletname, String walletPassword, String keystore, List<String> mnemonicList) {
        try {
            WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
            ECKeyPair keyPair = org.web3j.crypto.Wallet.decrypt(walletPassword, walletFile);

            String address = EthUtils.getAddress(keyPair);
            String keyStore = objectMapper.writeValueAsString(walletFile);
            //由于通过keyStore无法生成助记词，故恢复钱包助记词可为空，备份时不需要有助记词备份
            return new Wallet(walletname, walletPassword, null, address, keystore, keyStore, 1, mnemonicList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Wallet("", "", "", "", "", "", 1, null);
        }
    }

    /**
     * 生成钱包 以及keystore文件
     *
     * @param password
     * @param ecKeyPair
     * @param destinationDirectory
     * @param useFullScrypt
     * @return
     * @throws CipherException
     * @throws IOException
     */
    public static String generateWalletFile(
            String password, ECKeyPair ecKeyPair, File destinationDirectory, boolean useFullScrypt)
            throws CipherException, IOException {
        WalletFile walletFile = createWalletFile(password, ecKeyPair, useFullScrypt);
        String fileName = getWalletFileName(walletFile);
        File destination = new File(destinationDirectory, fileName);
        objectMapper.writeValue(destination, walletFile);
        return fileName;
    }

    /**
     * 生成keystore
     *
     * @param password
     * @param ecKeyPair
     * @param useFullScrypt
     * @return
     * @throws CipherException
     */
    public static WalletFile createWalletFile(String password, ECKeyPair ecKeyPair, boolean useFullScrypt) throws CipherException {
        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = org.web3j.crypto.Wallet.createStandard(password, ecKeyPair);
        } else {
            walletFile = org.web3j.crypto.Wallet.createLight(password, ecKeyPair);
        }
        return walletFile;
    }

    /**
     * 生成bip32 密钥
     *
     * @param mnemonic
     * @return
     */
    public static ECKeyPair generateBip32ECKeyPair(String mnemonic) {
        byte[] privateKeyBytes = KeyPairUtils.generatePrivateKey(mnemonic, KeyPairUtils.CoinTypes.ETH);
        return ECKeyPair.create(privateKeyBytes);
    }

    private static String getWalletFileName(WalletFile walletFile) {
        return walletFile.getAddress();
    }

    /**
     * 校验是否是以太坊地址
     *
     * @param input
     * @return
     */
    public static boolean isEthValidAddress(String input) {
        if (input.isEmpty() || !input.startsWith("0x")) {
            return false;
        }
        return isValidAddress(input);
    }

    /**
     * 校验地址
     *
     * @param address
     * @return
     */
    public static boolean isValidAddress(String address) {
        String cleanInput = Numeric.cleanHexPrefix(address);
        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }
        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }
}

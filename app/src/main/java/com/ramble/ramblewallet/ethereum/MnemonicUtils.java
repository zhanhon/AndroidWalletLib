package com.ramble.ramblewallet.ethereum;

import com.develop.mnemonic.crypto.SecureRandomUtils;
import com.ramble.ramblewallet.ethereum.utils.ChineseSimplified;
import com.ramble.ramblewallet.ethereum.utils.ChineseTraditional;
import com.ramble.ramblewallet.ethereum.utils.English;

import java.security.SecureRandom;
import java.util.ArrayList;

import static com.develop.mnemonic.MnemonicUtils.generateMnemonic;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/16
 */
public class MnemonicUtils {

    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    private MnemonicUtils() {
        throw new IllegalStateException("MnemonicUtils");
    }

    /**
     * 生成助记词：语言为简体中文\英文
     *
     * @return
     */
    public static ArrayList<String> generateMnemonicEnglishChinese() {
        byte[] initialEntropy = new byte[16];
        ArrayList<String> mnemonicList = new ArrayList<>();
        secureRandom.nextBytes(initialEntropy);
        String mnemonic1 = generateMnemonic(initialEntropy, English.INSTANCE);
        mnemonicList.add(mnemonic1);
        String mnemonic2 = generateMnemonic(initialEntropy, ChineseSimplified.INSTANCE);
        mnemonicList.add(mnemonic2);
        return mnemonicList;
    }

    /**
     * 生成助记词：语言为繁体中文
     *
     * @return
     */
    public static ArrayList<String> generateMnemonicChineseTraditional() {
        byte[] initialEntropy = new byte[16];
        ArrayList<String> mnemonicList = new ArrayList<>();
        secureRandom.nextBytes(initialEntropy);
        String mnemonic1 = generateMnemonic(initialEntropy, English.INSTANCE);
        mnemonicList.add(mnemonic1);
        String mnemonic2 = generateMnemonic(initialEntropy, ChineseTraditional.INSTANCE);
        mnemonicList.add(mnemonic2);
        return mnemonicList;
    }

}

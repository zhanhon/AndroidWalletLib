package com.ramble.ramblewallet.eth;

import com.develop.mnemonic.crypto.SecureRandomUtils;
import com.develop.mnemonic.wordlists.WordList;

import java.security.SecureRandom;

import static com.develop.mnemonic.MnemonicUtils.generateMnemonic;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/16
 */
public class MnemonicUtils {

    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    /**
     * 生成助记词
     *
     * @param type
     * @return
     */
    public static String generateMnemonicCustom(WordList type) {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        String mnemonic = generateMnemonic(initialEntropy, type);
        return mnemonic;
    }

}

package com.ramble.ramblewallet.tronsdk.common.bip32;

import android.util.Log;

/**
 * Assertion utility functions.
 */
public class Assertions {

    private Assertions() {
        throw new IllegalStateException("Assertions");
    }

    /**
     * Verify that the provided precondition holds true.
     *
     * @param assertionResult assertion value
     * @param errorMessage    error message if precondition failure
     */
    public static void verifyPrecondition(boolean assertionResult, String errorMessage) {
        if (!assertionResult) {
            Log.v("-=-=-=->", errorMessage);
        }
    }
}

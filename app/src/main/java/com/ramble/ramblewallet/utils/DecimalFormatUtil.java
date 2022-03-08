package com.ramble.ramblewallet.utils;

import java.text.DecimalFormat;

public class DecimalFormatUtil {

    private DecimalFormatUtil() {
        throw new IllegalStateException("DecimalFormatUtil");
    }

    public static final DecimalFormat format8 = new DecimalFormat("0.########");

    public static final DecimalFormat format2 = new DecimalFormat("0.##");

}

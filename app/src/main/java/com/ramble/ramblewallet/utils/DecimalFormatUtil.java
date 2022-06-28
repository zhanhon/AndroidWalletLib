package com.ramble.ramblewallet.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DecimalFormatUtil {

    public static final DecimalFormat format8 = new DecimalFormat("0.########");
    public static final DecimalFormat format2 = new DecimalFormat("0.##");

    private DecimalFormatUtil() {
        throw new IllegalStateException("DecimalFormatUtil");
    }

    public static String format(BigDecimal value, int maximum) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(maximum);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        return formater.format(value);
    }

}

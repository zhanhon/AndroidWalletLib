package com.ramble.ramblewallet.utils;

import android.text.TextUtils;

/**
 * @创建人： Ricky
 * @创建时间： 2022/2/15
 */
public class StringUtils {
    /**
     * 处理文本，将文本位数限制为maxLen，中文两个字符，英文一个字符
     *
     * @param content 要处理的文本
     * @param maxLen  限制文本字符数，中文两个字符，英文一个字符。例如：a啊b吧，则maxLen为6
     * @return
     */
    public static String handleText(String content, int maxLen) {
        if (TextUtils.isEmpty(content)) {
            return content;
        }
        int count = 0;
        int endIndex = 0;
        for (int i = 0; i < content.length(); i++) {
            char item = content.charAt(i);
            if (item < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
            if (maxLen == count || (item >= 128 && maxLen + 1 == count)) {
                endIndex = i;
            }
        }
        if (count <= maxLen) {
            return content;
        } else {
            //return content.substring(0, endIndex) + "...";//末尾添加省略号
            return content.substring(0, endIndex + 1);//末尾不添加省略号
        }
    }

    /**
     * 将每三个数字（或字符）加上逗号处理（通常使用金额方面的编辑）
     * 5000000.00 --> 5,000,000.00
     * 20000000 --> 20,000,000
     *
     * @param str 无逗号的数字
     * @return 加上逗号的数字
     */
    public static String strAddComma(String str) {
        if (str == null) {
            str = "";
        }
        String addCommaStr = ""; // 需要添加逗号的字符串（整数）
        String tmpCommaStr = ""; // 小数，等逗号添加完后，最后在末尾补上
        if (str.contains(".")) {
            addCommaStr = str.substring(0, str.indexOf("."));
            tmpCommaStr = str.substring(str.indexOf("."));
        } else {
            addCommaStr = str;
        }
        // 将传进数字反转
        String reverseStr = new StringBuilder(addCommaStr).reverse().toString();
        String strTemp = "";
        for (int i = 0; i < reverseStr.length(); i++) {
            if (i * 3 + 3 > reverseStr.length()) {
                strTemp += reverseStr.substring(i * 3);
                break;
            }
            strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
        }
        // 将 "5,000,000," 中最后一个","去除
        if (strTemp.endsWith(",")) {
            strTemp = strTemp.substring(0, strTemp.length() - 1);
        }
        // 将数字重新反转,并将小数拼接到末尾
        String resultStr = new StringBuilder(strTemp).reverse().toString() + tmpCommaStr;
        return resultStr;
    }
}
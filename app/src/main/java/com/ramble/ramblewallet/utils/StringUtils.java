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
}

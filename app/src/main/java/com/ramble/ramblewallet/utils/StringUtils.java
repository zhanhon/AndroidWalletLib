package com.ramble.ramblewallet.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ramble.ramblewallet.bean.Wallet;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @创建人： Ricky
 * @创建时间： 2022/2/15
 */
public class StringUtils {

    private StringUtils() {
        throw new IllegalStateException("StringUtils");
    }

    //小写字母
    public static final String REG_LOWERCASE = ".*[a-z]+.*";

    //根据对象的某个字段去重
    public static ArrayList<Wallet> removeDuplicateByAddress(ArrayList<Wallet> list) {
        ArrayList<Wallet> removedDuplicateList = list.stream().collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(comparing(n -> n.getAddress()))), ArrayList::new)
        );
        return removedDuplicateList;
    }

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

    public static boolean isHasLowerChar(String password) {
        return password.matches(REG_LOWERCASE);
    }

    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
        return pattern.matcher(str).matches();
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 传入区间值   假设:1-100   1-10
     *
     * @param edit    控件
     * @param context
     * @param max     最大数
     * @param min     最小数
     */
    public static void inputWatch(final EditText edit, final int max, final int min) {

        edit.addTextChangedListener(new TextWatcher() {
            int l = 0;
            int location = 0;//记录光标的位置
            String data;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                l = s.length();
                location = edit.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    Pattern p = Pattern.compile("[0-9]*");
                    Matcher m = p.matcher(s.toString());
                    if (m.matches()) {
                        int number = Integer.valueOf(s.toString());
                        if (number <= max && number >= min) {
                            data = s.toString();
                        } else {
                            edit.setText(data);
                            edit.setSelection(data.length());
                        }
                    } else {
                        edit.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //暂时不需要实现此方法
            }
        });
    }
}

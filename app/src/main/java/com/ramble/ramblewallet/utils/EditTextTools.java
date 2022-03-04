package com.ramble.ramblewallet.utils;

/**
 * @创建人： Ricky
 * @创建时间： 2022/3/4
 */

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextTools {

    EditText mEditText;
    int zhengshu = 10;
    int xiaoshu = 10;
    OnBeforeAfterChangedListener mOnBeforeAfterChangedListener;
    OnEmptyListener mOnEmptyListener;
    final TextWatcher mTextWatcher = new TextWatcher() {
        int zongdou = 0;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            zongdou = appearNumber(s.toString(), ",");
            int dou = 0;
            int chadou = 0;
            if (count != before) {
                String sss = "";
                String string = s.toString().replace(",", "");
                String string2 = "";
                int dianwei = string.indexOf('.');

                if (dianwei != -1) {
                    string2 = string.substring(dianwei);
                    string = string.substring(0, dianwei);
                    if (string2.length() > xiaoshu + 1) string2 = string2.substring(0, xiaoshu + 1);
                }

                if (string.length() > zhengshu) string = string.substring(0, zhengshu);
                int b = string.length() / 3;
                if (string.length() >= 0) {
                    int yushu = string.length() % 3;
                    if (yushu == 0) {
                        b = string.length() / 3 - 1;
                        yushu = 3;
                    }
                    for (int i = 0; i < b; i++) {
                        sss = sss + string.substring(0, yushu) + "," + string.substring(yushu, 3);
                        string = string.substring(3);
                    }
                    sss = sss + string;
                    mEditText.removeTextChangedListener(this);
                    if (dianwei != -1)
                        mEditText.setText(sss + string2);
                    else mEditText.setText(sss);
                    dou = appearNumber(mEditText.getText().toString(), ",");
                    chadou = dou - zongdou;
                    mEditText.addTextChangedListener(this);
                }

                Log.d(":::::::::::::::::", start + "   " + before + "    " + count);
            }
            if (start + count + chadou > mEditText.getText().length())
                mEditText.setSelection(mEditText.getText().length());
            else mEditText.setSelection(start + count + chadou);

            if (mOnBeforeAfterChangedListener != null) {
                mOnBeforeAfterChangedListener.onBeforeAfterChanged(mEditText.getText().toString());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {


            if (s.length() > 0) {
                if (mOnEmptyListener != null) {
                    mOnEmptyListener.onNoEmpty();
                }
            } else {
                if (mOnEmptyListener != null) {
                    mOnEmptyListener.onEmpty();
                }
            }


        }
    };

    public EditTextTools(EditText e, int zhengshu, int xiaoshu) {
        this.zhengshu = zhengshu;
        this.xiaoshu = xiaoshu;
        mEditText = e;
        mEditText.addTextChangedListener(mTextWatcher);
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
        this.mOnEmptyListener = onEmptyListener;
    }

    public void setOnBeforeAfterChangedListener(OnBeforeAfterChangedListener onBeforeAfterChangedListener) {
        this.mOnBeforeAfterChangedListener = onBeforeAfterChangedListener;
    }

    public interface OnBeforeAfterChangedListener {
        void onBeforeAfterChanged(String s);

    }

    public interface OnEmptyListener {
        void onEmpty();

        void onNoEmpty();

    }

}
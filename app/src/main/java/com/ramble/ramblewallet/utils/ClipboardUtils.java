package com.ramble.ramblewallet.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.ramble.ramblewallet.R;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/16
 */
public class ClipboardUtils {
    public static void copy(String value, Context context) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(null, value));
        Toast.makeText(context, context.getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
    }
}

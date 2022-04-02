package com.ramble.ramblewallet.wight;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qmuiteam.qmui.BuildConfig;


/**
 * 时间　: 2021/10/25 9:39
 * 作者　: potato
 * 描述　:
 */
public class HtmlWebView extends WebView {
    private static final String UTF8 = "UTF-8";
    private static final String BASEURL = "file:///android_asset/";
    private static final String MINETYPE = "text/html;charset=UTF-8";

    public HtmlWebView(Context context) {
        super(context);
        initWebViewSet();
    }

    public HtmlWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebViewSet();
    }

    public HtmlWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebViewSet();
    }

    private void initWebViewSet() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDefaultTextEncodingName(UTF8);
        settings.setBlockNetworkImage(false);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDefaultTextEncodingName("GBK");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setSavePassword(false);
        settings.setAllowFileAccess(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setTextZoom(100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // 开启调试
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * 加载数据
     *
     * @param htmlText html字符串
     */
    public void loadDataWithHtml(String htmlText) {
        loadDataWithBaseURL(BASEURL, htmlText, MINETYPE, null, null);
    }

}

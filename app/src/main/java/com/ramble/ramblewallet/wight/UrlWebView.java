/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * **************************************************************************************
 *
 *                         Website :                            *
 *
 * **************************************************************************************
 */
package com.ramble.ramblewallet.wight;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lxj.xpopup.XPopup;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.popup.CustomSSLPopup;


public class UrlWebView extends WebView implements DownloadListener {
    private MarginLayoutParams layoutParams;
    private int currentTop;
    private int startY;
    private int minTopMargin;
    private final int maxTopMargin;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private WebViewListener webViewListener;
    private boolean scrollable = false;
    public static final int FILE_CHOOSE_CODE = 321;
    public UrlWebView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        maxTopMargin = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        getSettings().setBlockNetworkImage(false);//解决图片不显示
        getSettings().setDomStorageEnabled(true);
        getSettings().setDefaultTextEncodingName("utf-8");

        setWebViewClient(client);
        setWebChromeClient(chromeClient);

        setDownloadListener(this);
    }

    public void setScrollable(boolean scrollable){
        this.scrollable = scrollable;
    }

    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object api){
        addJavascriptInterface(api,"app");
    }

    public void setWebViewListener(WebViewListener webViewListener) {
        this.webViewListener = webViewListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (scrollable && changed && minTopMargin == 0) {
            layoutParams = (MarginLayoutParams) this.getLayoutParams();
            minTopMargin = layoutParams.topMargin;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!scrollable || getScrollY() != 0) {
            return super.dispatchTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                break;

            case MotionEvent.ACTION_UP:
                currentTop = minTopMargin;
                layoutParams.setMargins(0, currentTop, 0, 0);
                setLayoutParams(layoutParams);
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getY() > startY) {
                    currentTop = Math.min((int) (minTopMargin + (event.getY() - startY) / 3), maxTopMargin);
                    layoutParams.setMargins(0, currentTop, 0, 0);
                    setLayoutParams(layoutParams);
                }
                if (event.getY() < startY) {
                    currentTop = Math.max((int) (minTopMargin - (startY - event.getY()) / 3), minTopMargin);
                    layoutParams.setMargins(0, currentTop, 0, 0);
                    setLayoutParams(layoutParams);
                }
                break;
        }

        if (currentTop != 0 && currentTop != minTopMargin) {
            return false;
        }

        return super.dispatchTouchEvent(event);
    }

    private WebViewClient client = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
            Context context = getContext();
            new XPopup.Builder(context)
                    .shadowBgColor(context.getColor(R.color.color_66000000))
                    .asCustom(new CustomSSLPopup(context, context.getString(R.string.system_prompt),
                            context.getString(R.string.ssl_error), context.getString(R.string.cancel),
                            context.getString(R.string.confirm),handler))
                    .show();
        }
    };

    private WebChromeClient chromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            webViewListener.onProgressChanged(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams params) {
            Context context = getContext();
            uploadMessageAboveL = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,params.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE);
            intent.setType("*/*");
            ((Activity)getContext()).startActivityForResult(Intent.createChooser(intent, context.getString(R.string.label_file_selector)), 321);
            return true;
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            webViewListener.onReceivedTitle(title);
        }
    };

    public void onReceiveFile(Uri uri){
        uploadMessageAboveL.onReceiveValue(new Uri[]{uri});
        uploadMessageAboveL = null;
    }

    public void onCancelChooseFile(){
        uploadMessageAboveL.onReceiveValue(null);
        uploadMessageAboveL = null;
    }

    public void setTextZoom(int size) {
        getSettings().setTextZoom(size);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {


    }

}

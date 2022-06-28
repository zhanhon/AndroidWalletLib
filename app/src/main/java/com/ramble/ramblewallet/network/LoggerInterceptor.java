package com.ramble.ramblewallet.network;


import androidx.annotation.NonNull;

import com.ramble.ramblewallet.utils.WalletLogger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class LoggerInterceptor implements Interceptor {

    private final HttpLoggingInterceptor httpLoggingInterceptor;
    public LoggerInterceptor(){
        this.httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String s) {
                WalletLogger.i("wallet",WalletLogger.formatDataFromJson(s));
            }
        });
        //四个等级
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return httpLoggingInterceptor.intercept(chain);
    }
}

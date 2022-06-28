package com.ramble.ramblewallet.network;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.hjq.language.MultiLanguages;
import com.ramble.ramblewallet.MyApp;
import com.ramble.ramblewallet.constant.*;
import com.ramble.ramblewallet.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author ChayChan
 * @date 2017/6/10  10:54
 */

public class ApiRetrofit {

    private static ApiRetrofit mApiRetrofit;
    private final Retrofit mRetrofit;
    private final OkHttpClient mClient;
    private final ApiService mApiService;

    private static class TokenInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {

            String token = SharedPreferencesUtils.getAccessToken();

            //添加 应用语言环境
            String lang = "";
            int languages= SharedPreferencesUtils.getLanguages();
            if(languages==0){
                Locale locale= MultiLanguages.getAppLanguage();
                if(locale.getLanguage().contains("zh")){
                    //中文
                    lang = "zh";
                }else {
                    lang = "en";
                }
            }else {
                if(languages==1){
                    //中文
                    lang = "zh";
                }else {
                    //英文
                    lang = "en";
                }
            }
            if(token==null){
                Request request = chain.request().newBuilder()
                        .addHeader(ConstantsKt.ATTR_LANGUES, lang)
                        .addHeader(ConstantsKt.ATTR_CLIENT_TYPE, "1")
                        .build();
                return chain.proceed(request);
            }

            Request request = chain.request().newBuilder()
                    .addHeader(ConstantsKt.ATTR_HEADER_TOKEN,token)
                    .addHeader(ConstantsKt.ATTR_LANGUES, lang)
                    .addHeader(ConstantsKt.ATTR_CLIENT_TYPE, "1")
                    .build();

            return chain.proceed(request);
        }
    }


    public ApiRetrofit() {
        GsonBuilder builder = new GsonBuilder()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {

                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.serialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });

        //cache url
        File httpCacheDirectory = new File(MyApp.Companion.getGetInstance().sInstance.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        mClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor())//添加log拦截器
                .addInterceptor(new TokenInterceptor())
                //.sslSocketFactory(createSSLSocketFactory())
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(FastUrlTask.INSTANCE.getFastUrl(ConstantsKt.getBaseServerUrl()))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持RxJava
                .client(mClient)
                .build();

        mApiService = mRetrofit.create(ApiService.class);
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    public static ApiRetrofit getInstance() {
        if (mApiRetrofit == null) {
            synchronized (Object.class) {
                if (mApiRetrofit == null) {
                    mApiRetrofit = new ApiRetrofit();
                }
            }
        }
        return mApiRetrofit;
    }

    public ApiService getApiService() {
        return mApiService;
    }

}

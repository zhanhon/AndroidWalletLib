package com.ramble.ramblewallet.network;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.ramble.ramblewallet.MyApp;
import com.ramble.ramblewallet.utils.Md5Util;
import com.ramble.ramblewallet.utils.SharedPreferencesUtils;

import java.io.File;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.ramble.ramblewallet.constant.ConstantsKt.CN;
import static com.ramble.ramblewallet.constant.ConstantsKt.LANGUAGE;
import static com.ramble.ramblewallet.constant.ConstantsKt.getAppContext;

/**
 * @author ChayChan
 * @date 2017/6/10  10:54
 */

public class ApiRetrofit {

    private static ApiRetrofit mApiRetrofit;
    private final Retrofit mRetrofit;
    private final OkHttpClient mClient;
    private final ApiService mApiService;

    /**
     * 请求访问quest和response拦截器
     */
    private final Interceptor mLogInterceptor = chain -> {
        Request request = chain.request();
        Response response = chain.proceed(chain.request());
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        Log.v("-=-=->", "=========================Response End==========================");
        Log.v("-=-=->", content);

        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    };

    /**
     * 增加头部信息的拦截器
     */
    private final Interceptor mHeaderInterceptor = chain -> {
        String languageCode = SharedPreferencesUtils.getString(getAppContext(), LANGUAGE, CN);
        Request.Builder builder = chain.request().newBuilder();
        Request request = chain.request();
        String url = request.url().toString()
                .replace(request.url().host() + ":" + request.url().port(), "")
                .replace(request.url().host(), "")
                .replace("http://", "")
                .replace("https://", "");
        String signStr = url + System.currentTimeMillis() + "1" + "" + AppUtils.getSecretKey();
        String sign = Md5Util.md5(signStr);
        builder.addHeader("apiName", url); //API接口名
        builder.addHeader("callTime", String.valueOf(System.currentTimeMillis())); //调用时间
        builder.addHeader("sign", sign);
        builder.addHeader("clientType", "1"); //"客户端类型|1:Android|2:IOSv|3:H5|4:PC"
        builder.addHeader("languageCode", languageCode); //语言代码|zh_CN:简体中文|zh_TW:繁体中文|en:英文|th:泰语|vi:越南语
        builder.addHeader("apiVersion", "20211227"); //(预留字段)Api版本号
        builder.addHeader("gzipEnabled", "0"); //(预留字段)是否启用gzip压缩｜0:不启用｜1:启用


        return chain.proceed(builder.build());
    };

    public ApiRetrofit() {
        //cache url
        File httpCacheDirectory = new File(MyApp.sInstance.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        mClient = new OkHttpClient.Builder()
                //.addInterceptor(mHeaderInterceptor)//添加头部信息拦截器
                .addInterceptor(mLogInterceptor)//添加log拦截器
                .sslSocketFactory(createSSLSocketFactory())
                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://13.214.207.185:8095/") //开发环境：http://13.229.173.84:8095/
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
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

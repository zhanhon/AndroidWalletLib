package com.ramble.ramblewallet.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ramble.ramblewallet.utils.Zlib;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import kotlin.collections.ArraysKt;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.ByteString;
import retrofit2.HttpException;
import static com.bumptech.glide.load.Key.CHARSET;

import static com.ramble.ramblewallet.constant.ConstantsKt.DEF_HEAD;
import static com.ramble.ramblewallet.network.StoreUtilsKt.md55;

/**
 * 时间　: 2021/12/21 13:15
 * 作者　: potato
 * 描述　:
 */
public class ApiNameInterceptor implements Interceptor {
    final private String[] whiteArray = new String[]{
            "common-api/system/getSecret",
            "config-api/appVersion/queryNewestVersion",
            "common-api/upload/uploadUserImage",
            "common-api/upload/batchUploadUserImage"
    };
    final private String[] whiteArray2 = new String[]{
            "common-api/system/getSecret",
            "config-api/appVersion/queryNewestVersion"
    };
    private final ArrayList<ResponseTime> arrayList = new ArrayList();

    /**
     * 根据String型时间，获取long型时间，单位毫秒
     *
     * @param inVal 时间字符串
     * @return long型时间
     */
    private long fromDateStringToLong(String inVal) {
        Date date = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        try {
            date = inputFormat.parse(inVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    @SuppressLint("CheckResult")
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString()
                .replace(request.url().host() + "/", "")
                .replace("http://", "")
                .replace("https://", "");
        Response response = null;
        if (url != null) {
            long startTime = fromDateStringToLong(
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date())
            );
            arrayList.add(new ResponseTime(url, startTime));
        }
        if (ArraysKt.any(whiteArray, url::contains) || request == null || request.body() == null) {
            if (ArraysKt.any(whiteArray2, url::contains)) {
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                String requestContent = buffer.readUtf8().replace(/*BuildConfig.DEFAULTAPINAME*/DEF_HEAD, url);
                try {
                    JSONObject jObject = new JSONObject(requestContent);
                    JSONObject jHeader = jObject.getJSONObject("header");
                    jHeader.put("gzipEnabled", 0);
                    jObject.put("header", jHeader);
                    Request.Builder requestBuilder = request.newBuilder().method(
                            request.method(),
                            RequestBody.create(request.body().contentType(), ByteString.encodeUtf8(jObject.toString()))
                    );
                    response = chain.proceed(requestBuilder.build());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                response = chain.proceed(request);
            }
        } else {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            String requestContent = buffer.readUtf8().replace(/*BuildConfig.DEFAULTAPINAME*/DEF_HEAD, url);
            Request.Builder requestBuilder;

            try {
                if (!url.contains(DEF_HEAD)) {
//                    LogUtil.i("encryptBody url = " + url);
                    response = encryptBody(chain, request, requestContent);
                } else {
                    requestBuilder = request.newBuilder().method(
                            request.method(),
                            RequestBody.create(request.body().contentType(), ByteString.encodeUtf8(requestContent))
                    );
                    response = chain.proceed(requestBuilder.build());
                }
            } catch (Exception e) {

                Log.v("---------->e3:", e.getMessage());


                if (e instanceof HttpException) {
                    throw new IOException("网络不给力，请检查网络重试(1001)");
                } else  if (e instanceof SocketTimeoutException) {
                    throw new IOException("网络不给力，请检查网络重试(1003)");
                } else if (e instanceof SocketException) {
                    throw new IOException("网络不给力，请检查网络重试(1004)");
                } else if (e instanceof UnknownHostException) {
                    throw new IOException("网络不给力，请检查网络重试(1005)");
                } else {
                    throw new IOException("网络不给力，请检查网络重试");
                }
            }
        }
        return filterResponse(response);
    }

    @NotNull
    private Response encryptBody(Chain chain, Request request, String requestContent) throws Exception {
        ////////// request //////////
        JSONObject jObject = new JSONObject(requestContent);
        JSONObject jBody = jObject.getJSONObject("body");
        JSONObject jHeader = jObject.getJSONObject("header");
        String aesJBody;
        Zlib.setGzipEnabled(1);
        byte[] jzlibData = Zlib.jzlib(jBody.toString().getBytes(CHARSET));
        aesJBody = ObjUtils.INSTANCE.getAesCbcEncryptor().encrypt(
                jzlibData,
                "RxViewModel.globe.getAesSecret()"
        );
        jHeader.put("gzipEnabled", 1);
        jHeader.put("sign", md5(jHeader, aesJBody));
        jObject.put("header", jHeader);
        jObject.put("body", aesJBody);

        Request.Builder requestBuilder = request.newBuilder().method(
                request.method(),
                RequestBody.create(request.body().contentType(), ByteString.encodeUtf8(jObject.toString()))
        );

        ////////// response //////////
        Response response = chain.proceed(requestBuilder.build());
        jObject = new JSONObject(response.body().string());
        jBody = jObject.getJSONObject("body");

        jHeader = jObject.getJSONObject("header");
        aesJBody = jBody.getString("data");

        if (TextUtils.isEmpty(aesJBody) || aesJBody.equals("null")) {
            jBody.put("data", null);
        } else {
            try {
                byte[] dataJson = ObjUtils.INSTANCE.getAesCbcEncryptor().decryptByte(
                        aesJBody,
                        "RxViewModel.globe.getAesSecret()"
                );
                String unjzlibData = new String(Zlib.unjzlib(dataJson), CHARSET);
                if (unjzlibData.startsWith("[")) {
                    jBody.put("data", new JSONArray(unjzlibData));
                } else {
                    jBody.put("data", new JSONObject(unjzlibData));
                }
            } catch (Exception e) {
                Log.v("---------->e4:", e.getMessage());
                e.printStackTrace();
            }
        }
        ResponseBody body = ResponseBody.create(response.body().contentType(), jObject.toString());
        response = response.newBuilder().body(body).build();
        return response;
    }

    private String md5(JSONObject jHeader, String aesJBody) throws JSONException {
        return md55(
                String.valueOf(jHeader.getInt("clientType")) +
                        jHeader.getLong("platformId") +
                        jHeader.getString("apiName") +
                        jHeader.getString("token") +
                        jHeader.getLong("callTime") +
                        aesJBody +
                       " RxViewModel.globe.getSignSecret()"
        );
    }

    /**
     * 做业务响应的统一拦截，如限制访问、挂维护等<br/>
     * 注：该方法不涉及加解密，入参 originalResponse 一定要是 已解密或无需解密
     *
     * @param originalResponse
     * @return
     * @throws IOException
     */
    private Response filterResponse(Response originalResponse) throws IOException {
        if (originalResponse == null || originalResponse.body() == null
                || originalResponse.body().contentType() == null
                || !originalResponse.body().contentType().toString().contains("json")) {//只处理json的响应
            return originalResponse;
        }

        String apiName = originalResponse.request().url().url().getPath().replaceFirst("/", "");
        String originalResponseStr = originalResponse.body().string();
        originalResponseStr = originalResponseStr.replace(DEF_HEAD, apiName);

        try {//注：目前只处理业务响应的json，根据code字段作“业务过滤”，所以这里要自己管理JSONException
            JSONObject jObject = new JSONObject(originalResponseStr);
            JSONObject jBody = jObject.getJSONObject("body");
            JSONObject jHeader = jObject.getJSONObject("header");

            String keyCode = "code", keyMessage = "message";
            if (jBody.getInt(keyCode) == 1015) {//IP访问被拒绝
//                ActivityManager manager = MyApp.Companion.getAppComponent().activityManager();
//                Activity activity = manager.getTopActivityOrNull();
//                if (activity != null) {
//                    activity.startActivity(new Intent(activity, IpForbiddenActivity.class));
//                }
//                throw new SourceException(jBody.optString(keyMessage), 1015);
            }
            if (jBody.getInt(keyCode) == 1016) {//平台维护中
//                ActivityManager manager = MyApp.Companion.getAppComponent().activityManager();
//                Activity activity = manager.getTopActivityOrNull();
//                if (activity != null) {
//                    String data = jBody.getString("data");
//                    MaintainInfo info = MyApp.Companion.getAppComponent().moshi().adapter(MaintainInfo.class).fromJson(data);
//                    Intent intent = new Intent(activity, MaintainDialogActivity.class);
//                    intent.putExtra("maintain", info);
//                    activity.startActivity(intent);
//                }
//                throw new SourceException(jBody.optString(keyMessage), 1016);
            }

            for (int i = 0; i < arrayList.size(); i++) {
                if ((arrayList.get(i) == null) || (arrayList.get(i).getUrl() == null)) {
                    continue;
                }
                if (apiName.equals(arrayList.get(i).getUrl())) {
                    long stopTime = fromDateStringToLong(
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date())
                    );
                    //计算时间差,单位毫秒
                    long timeSpan = stopTime - arrayList.get(i).getTime();
//                    SaveFile.saveFile.Log("http请求接口："
//                            + apiName
//                            + ", responseTime:"
//                            + timeSpan + "ms"
//                            + ", reqTid:" + new Date().getTime()
//                            + "-" + UUID.randomUUID().toString()
//                            + ", callTime:" + System.currentTimeMillis() + ","
//                            + " requestId:" + jHeader.getString("requestId")//Header可能无此字段JSONObject get会异常
//                    );
                }
            }

        } catch (JSONException jsonE) {
            //这里可不作处理
        }

        return originalResponse.newBuilder()
                .body(ResponseBody.create(originalResponse.body().contentType(), originalResponseStr))
                .build();
    }

    public class GetSecretException extends RuntimeException {
    }
}


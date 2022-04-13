package com.ramble.ramblewallet.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okio.ByteString
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * 时间　: 2021/12/21 14:01
 * 作者　: potato
 * 描述　:
 */

@JvmOverloads
fun toJson(obj: Any, type: Type? = null, moshi: Moshi? = null): String {
    val mMoshi = moshi ?: Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val mType = type ?: obj.javaClass
    return mMoshi.adapter<Any>(mType).lenient().serializeNulls().nullSafe().toJson(obj)
}

/**
 * Type listMyData = Types.newParameterizedType(List.class, MyData.class);
 * JsonAdapter<List<MyData>> adapter = moshi.adapter(listMyData);
 * NOTE:
 * String json = "[{ "name" : "foo" },{ "name" : "bar"}]";
 * @link https://github.com/square/moshi/issues/81
 * @link https://github.com/square/moshi/issues/78
 */
@JvmOverloads
@Throws(IOException::class)
@Suppress("UNCHECKED_CAST")
fun <T> fromJson(json: String, type: Type, moshi: Moshi? = null): T {
    val mMoshi = moshi ?: Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    return mMoshi!!.adapter<T>(type).lenient().serializeNulls().nullSafe().fromJson(json)!!
}

fun <T : ApiRequest.Body> T.toApiRequest(apiName: String): ApiRequest<T> {
    return ObjUtils.apiRequest(this, apiName)
}
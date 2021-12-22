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
fun cancelOkHttp3Request(client: OkHttpClient, tag: Any) {
    client.dispatcher().queuedCalls()
        .filter { tag == it.request().tag() }
        .forEach { it.cancel() }
    client.dispatcher().runningCalls()
        .filter { tag == it.request().tag() }
        .forEach { it.cancel() }
}

fun cancelOkHttp3Request(client: OkHttpClient) {
    client.dispatcher().queuedCalls().forEach { it.cancel() }
    client.dispatcher().runningCalls().forEach { it.cancel() }
}

@JvmOverloads
@Suppress("UNCHECKED_CAST")
fun toMap(obj: Any, moshi: Moshi? = null): Map<String, String> {
    val mMoshi = moshi ?: Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val adapter = mMoshi!!.adapter(obj.javaClass).lenient().serializeNulls().nullSafe()
    return adapter.toJsonValue(obj) as Map<String, String>
}

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

@JvmOverloads
fun toBytesJson(obj: Any, type: Type? = null, moshi: Moshi? = null): ByteArray {
    return toJson(obj, type, moshi).toByteArray()
}

@JvmOverloads
@Throws(IOException::class)
fun <T> fromBytesJson(bytes: ByteArray, type: Type, moshi: Moshi? = null): T {
    return fromJson(bytes.utf8String(), type, moshi)
}

fun ByteArray.utf8String() = this.toString(Charsets.UTF_8)

fun String.md55() = ByteString.encodeUtf8(this).md5().hex()!!

fun secondsToMillis(seconds: Int): Long {
    return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds.toLong())
}

fun minutesToMillis(minutes: Int): Long {
    return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes.toLong())
}

fun hoursToMillis(hours: Int): Long {
    return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(hours.toLong())
}

fun daysToMillis(days: Int): Long {
    return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())
}


fun <T : ApiRequest.Body> T.toApiRequest(): ApiRequest<T> {
    return ObjUtils.apiRequest(this)
}
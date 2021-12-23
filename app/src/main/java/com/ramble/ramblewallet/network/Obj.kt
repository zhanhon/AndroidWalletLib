package com.ramble.ramblewallet.network

import com.squareup.moshi.Json
import java.io.IOException

/**
 * 时间　: 2021/12/23 13:33
 * 作者　: potato
 * 描述　:
 */
interface Envelope<T> {
    fun status(): Boolean
    fun code(): Int
    fun message(): String
    fun data(): T?
}

class DiskCacheEntry(
    @JvmField @Json(name = "data") val data: ByteArray,
    @JvmField @Json(name = "TTL") val TTL: Long,
    @JvmField @Json(name = "softTTL") val softTTL: Long
) {
    fun isExpired() = TTL < System.currentTimeMillis()

    fun refreshNeeded() = softTTL < System.currentTimeMillis()

    override fun toString() = toJson(this, DiskCacheEntry::class.java)
}

class MemoryCacheEntry(
    @JvmField @Json(name = "data") val data: Any?,
    @JvmField @Json(name = "TTL") val TTL: Long
) {
    fun isExpired() = TTL < System.currentTimeMillis()
}

class Source<T> internal constructor(
    @JvmField val status: Status,
    @JvmField val error: Throwable?,
    @JvmField val data: T?
) {
    companion object {
        fun <T> inProgress(): Source<T> {
            return Source(Status.IN_PROGRESS, null, null)
        }

        fun <T> success(data: T): Source<T> {
            return Source(Status.SUCCESS, null, data)
        }

        fun <T> error(error: Throwable): Source<T> {
            return Source(Status.ERROR, error, null)
        }

        fun <T> irrelevant(): Source<T> {
            return Source(Status.IRRELEVANT, null, null)
        }

        fun <T, R> success(upSource: Source<T>, creator: ((src: Source<T>) -> Source<R>)): Source<R> {
            return when (upSource.status) {
                Status.IRRELEVANT -> Source.irrelevant()
                Status.ERROR -> Source.error(upSource.error!!)
                Status.IN_PROGRESS -> Source.inProgress()
                Status.SUCCESS -> creator(upSource)
            }
        }
    }

    fun isSuccess(): Boolean = status == Status.SUCCESS

    fun isError(): Boolean = status == Status.ERROR

    fun isInProgress(): Boolean = status == Status.IN_PROGRESS

    fun isIrrelevant(): Boolean = status == Status.IRRELEVANT
}

enum class Status {
    SUCCESS, ERROR, IN_PROGRESS, IRRELEVANT
}

class SourceException : IOException {
    @JvmField
    val code: Int

    constructor(code: Int = -1) : super() {
        this.code = code
    }

    constructor(message: String?, code: Int = -1) : super(message) {
        this.code = code
    }

    constructor(cause: Throwable?, code: Int = -1) : super(cause) {
        this.code = code
    }

    constructor(message: String?, cause: Throwable?, code: Int = -1) : super(message, cause) {
        this.code = code
    }

    companion object {
        fun isSourceException(error: Throwable): Boolean {
            return error is SourceException
        }

        fun from(error: Throwable): SourceException {
            return error as? SourceException ?: SourceException(error.message, error.cause)
        }

        fun from(envelope: Envelope<*>): SourceException {
            return SourceException(envelope.message(), envelope.code())
        }
    }
}
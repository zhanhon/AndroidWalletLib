package com.ramble.ramblewallet.utils

import io.reactivex.Observable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

/**
 * 时间　: 2021/12/21 13:47
 * 作者　: potato
 * 描述　:
 */
fun jdk8DateTime(): String {
    return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
        LocalDateTime.now().atZone(ZoneId.systemDefault())
    )
}

fun jdk8DateTime2(): ZonedDateTime {
    return LocalDateTime.now().atZone(ZoneId.systemDefault())
}

fun Boolean.mapInt(): Int = if (this) 1 else 0

fun CharSequence?.isLongerThan(other: CharSequence?) = lengthOrZero() > other.lengthOrZero()

fun CharSequence?.lengthOrZero() = this?.length ?: 0

//@JvmOverloads
//fun String.doSplit(regex: Regex = COMMA_STR_REG): List<String> {
//    return this.split(regex).dropWhile { it.isNullOrEmpty() }.dropLastWhile { it.isNullOrEmpty() }
//}

//fun String.doSplit(regex: String) = this.doSplit(regex.toRegex())

fun String.prettyCardNumber(): String {
    val length = this.length
    return if (length <= 4) {
        "**** **** **** $this"
    } else {
        return "**** **** **** ${this.substring(length - 4, length)}"
    }
}

fun String.prettyPlayName(): String {
    return this.replace("(", "\n(").replace("（", "\n（")
}

fun String.safeToInt(): Int = if (this.isNullOrEmpty()) 0 else this.toInt()

fun String.safeToLong(): Long = if (this.isNullOrEmpty()) 0L else this.toLong()

fun String.safeToFloat(): Float = if (this.isNullOrEmpty()) 0.00f else this.toFloat()

fun String.safeToDouble(): Double = if (this.isNullOrEmpty()) 0.00 else this.toDouble()

//fun <T> Throwable.errorSource(): Source<T> {
//    if (isDebug) {
//        this.printStackTrace()
//    }
//    return Source.error(this)
//}

//fun <T> ApiResponse<T>.flatMapError(): Observable<T> {
//    return Observable.error(SourceException(this.message(), this.code()))
//}
//
//fun <T> ApiResponse<T>.flatMapSuccess(): Observable<T> {
//    return Observable.just(this.data() ?: Any() as T)
//}
//
//fun <T> ApiResponse<T>.flatMap(): Observable<T> {
//    return if (status()) this.flatMapSuccess() else this.flatMapError()
//}

class Zlib {
    companion object {

        private var isGzip = 0 //是否需要解压缩

        @JvmStatic
        fun setGzipEnabled(isGzipParameter: Int) {
            isGzip = isGzipParameter
        }

        @JvmStatic
        fun getGzipEnabled(): Int {
            return isGzip
        }

        /**
         * 压缩数据
         */
        @JvmStatic
        fun jzlib(objByteArray: ByteArray?): ByteArray? {
            var data: ByteArray? = null
            try {
                val out = ByteArrayOutputStream()
                val zOut = DeflaterOutputStream(out)
                val objOut = DataOutputStream(zOut)
                objOut.write(objByteArray)
                objOut.flush()
                zOut.close()
                data = out.toByteArray()
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return data
        }

        /**
         * 解压数据
         */
        @JvmStatic
        fun unjzlib(objByteArray: ByteArray?): ByteArray? {
            var data: ByteArray? = null
            try {
                val `in` = ByteArrayInputStream(objByteArray)
                val zIn = InflaterInputStream(`in`)
                val buf = ByteArray(1024 * 3)
                var num = -1
                val baos = ByteArrayOutputStream()
                while (zIn.read(buf, 0, buf.size).also { num = it } != -1) {
                    baos.write(buf, 0, num)
                }
                data = baos.toByteArray()
                baos.flush()
                baos.close()
                zIn.close()
                `in`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return data
        }
    }
}

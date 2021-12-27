package com.ramble.ramblewallet.utils

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


class Zlib {
    companion object {

        private var isGzip = 0 //是否需要解压缩
        private var apiName = ""



        @JvmStatic
        fun setGzipEnabled(isGzipParameter: Int) {
            isGzip = isGzipParameter
        }

        @JvmStatic
        fun getGzipEnabled(): Int {
            return isGzip
        }

        @JvmStatic
        fun setApiName(apiName: String) {
            this.apiName = apiName
        }

        @JvmStatic
        fun getApiName(): String {
            return apiName
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

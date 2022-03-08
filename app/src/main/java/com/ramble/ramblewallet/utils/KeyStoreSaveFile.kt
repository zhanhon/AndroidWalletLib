package com.ramble.ramblewallet.utils

import android.os.Environment
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

class KeyStoreSaveFile {
    private lateinit var calendar: Calendar
    private var savePath: String? = null
    private var logFile: String? = null
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var min = 0
    private var sec = 0
    private var msec = 0

    fun content(walletName: String, content: String?) {
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
        hour = calendar.get(Calendar.HOUR_OF_DAY) //24小时制
        min = calendar.get(Calendar.MINUTE)
        sec = calendar.get(Calendar.SECOND)
        savePath =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "RambleWallet/"
        logFile = walletName + "_" + "keystore.txt"

        hour = calendar.get(Calendar.HOUR_OF_DAY) //24小时制
        min = calendar.get(Calendar.MINUTE)
        sec = calendar.get(Calendar.SECOND)
        msec = calendar.get(Calendar.MILLISECOND)
        var str: String = String.format("%s\n", content)
        val destDir = File(savePath.toString())
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        writeToFile(str, savePath.toString(), logFile.toString())
    }

    private fun writeToFile(content: String, filePath: String, fileName: String) {
        try {
            val file = RandomAccessFile(filePath + fileName, "rw")
            val currentLength: Long = file.length()
            if (currentLength + content.length > 10 * 1024 * 1024) {
                file.setLength(0)
            }
            file.seek(currentLength)
            file.write(content.toByteArray())
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmField
        var saveFile: KeyStoreSaveFile =
            KeyStoreSaveFile()
    }
}
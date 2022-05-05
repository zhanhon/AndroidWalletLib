package com.ramble.ramblewallet.update

import com.ramble.ramblewallet.BuildConfig
import org.lzh.framework.updatepluginlib.UpdateBuilder
import org.lzh.framework.updatepluginlib.UpdateConfig
import org.lzh.framework.updatepluginlib.base.UpdateParser
import org.lzh.framework.updatepluginlib.model.CheckEntity
import org.lzh.framework.updatepluginlib.model.Update

class UpdateUtils {

    fun checkUpdate(newVersionData: AppVersion?, title: String, cancel: Boolean, first: Boolean) {
        val allDialogShowStrategy = DialogShowStrategy()
        UpdateConfig.getConfig()
            .setCheckWorker(OkhttpCheckWorker::class.java)
            .setDownloadWorker(OkhttpDownloadWorker::class.java)
            .setDownloadNotifier(AkDownloadNotifier())
            .setCheckNotifier(AKCheckNotifier(null, title, first))
            .setFileChecker(FileChecker()).checkEntity = CheckEntity()
            .setMethod("POST")
            .setUrl("go:")
        UpdateBuilder.create().setUpdateStrategy(allDialogShowStrategy)
            .setUpdateParser(object : UpdateParser() {
                @Throws(Exception::class)
                override fun parse(response: String): Update {
                    val update = UpdateImp()
                    update.updateUrl = newVersionData?.updateUrl
                    update.versionCode = BuildConfig.VERSION_CODE + 1//模拟一个versioncode
                    update.versionName = newVersionData?.version
                    update.updateContent = newVersionData?.content
                    when (newVersionData?.forcedUpdatingShow) {
                        1 -> update.isForced = true
                        else -> update.isForced = false
                    }
                    //用isIgnore属性代表取消按钮
                    update.isIgnore = cancel
                    return update
                }
            }).check()
    }

}
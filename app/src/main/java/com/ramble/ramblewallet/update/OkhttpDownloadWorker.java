package com.ramble.ramblewallet.update;

import org.lzh.framework.updatepluginlib.base.DownloadWorker;
import org.lzh.framework.updatepluginlib.impl.ForcedUpdateStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 一个简单的的OkHtp文件下载任务。
 */
public class OkhttpDownloadWorker extends DownloadWorker {
    public static Call call;

    @Override
    protected void download(String url, File target) throws Exception {
        // 在此进行文件下载任务。运行于子线程中。需要同步请求。
        Request.Builder builder = new Request.Builder().url(url);
        Request request = builder.get().build();
        call = OkhttpCheckWorker.sOkClient.newCall(request);
        target.delete();
        Response response = call.execute();
        int code = response.code();
        if (code < 200 || code >= 300) {
            // 通过抛出异常中断下载任务并提示用户下载失败
            throw new RuntimeException("下载失败");
        }
        long contentLength = response.body().contentLength();
        int bufLength = -1;
        InputStream inputStream = response.body().byteStream();
        OutputStream output = new FileOutputStream(target, false);
        byte[] buffer = new byte[8 * 1024];
        long start = System.currentTimeMillis();
        int offset = 0;
        while ((bufLength = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bufLength);
            output.flush();
            offset += bufLength;
            long end = System.currentTimeMillis();
            if (end - start > 1000) {
                // 发送下载进度信息。便于用于更新界面等操作
                sendDownloadProgress(offset, contentLength);
            }
        }
        Proxy();
        // 下载完成。通知下载完成
        sendDownloadComplete(target);
    }


    private void Proxy() {
        if (builder.getUpdateStrategy() instanceof ForcedUpdateStrategy) {
            builder.setUpdateStrategy(new LotForcedUpdateStrategy(builder.getUpdateStrategy()));
        } else {
            builder.setUpdateStrategy(builder.getUpdateStrategy());
        }
    }

}

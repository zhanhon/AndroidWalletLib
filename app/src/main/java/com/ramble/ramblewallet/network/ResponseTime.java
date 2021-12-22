package com.ramble.ramblewallet.network;

/**
 * 时间　: 2021/12/21 13:16
 * 作者　: potato
 * 描述　:
 */
public class ResponseTime {
    private String url;
    private long time;

    public ResponseTime(String url, long time) {
        this.url = url;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

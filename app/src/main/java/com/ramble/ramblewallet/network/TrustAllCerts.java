package com.ramble.ramblewallet.network;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * @创建人： Ricky
 * @创建时间： 2021/7/20
 */
public class TrustAllCerts implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        for (X509Certificate cert : chain) {
            try {
                cert.checkValidity();
                cert.verify(cert.getPublicKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

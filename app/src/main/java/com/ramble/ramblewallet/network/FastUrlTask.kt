package com.ramble.ramblewallet.network

import com.ramble.ramblewallet.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.platform.Platform
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLContext as SSLContext1


object FastUrlTask {
    private const val timeOut = 5000L
    private val executor = Executors.newFixedThreadPool(BuildConfig.BASE_SERVER_URL.size)
    private val checkConnectClient = OkHttpClient.Builder()
        .sslSocketFactory(
            provideSSLContext(provideX509TrustManager()).socketFactory,
            provideX509TrustManager()
        )
        .hostnameVerifier { _, _ -> true }
        .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
        .build()

    private fun provideSSLContext(x509TrustManager: X509TrustManager): SSLContext1 {
        val sslContext: SSLContext1
        try {
            sslContext = Platform.get().sslContext
            sslContext.init(null, arrayOf<TrustManager>(x509TrustManager), SecureRandom())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: KeyManagementException) {
            throw RuntimeException(e)
        }
        return sslContext
    }

    private fun provideX509TrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()

            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }
        }
    }

    fun getFastUrl(urls: List<String>): String {
        if (urls.size == 1) return urls.first()
        val tasks = urls.map { url ->
            Callable {
                val request = Request.Builder()
                    .url("$url/wallet-decentralized-api/actuator/health")
                    .get()
                    .build()
                try {
                    checkConnectClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful && response.body() != null) {
                            return@Callable url
                        }
                    }
                } catch (e: Exception) {
                }
                Thread.sleep(timeOut)
                return@Callable ""
            }
        }

        val fastUrl = executor.invokeAny(tasks)
        return if (fastUrl.isEmpty()) urls.random() else fastUrl
    }

}
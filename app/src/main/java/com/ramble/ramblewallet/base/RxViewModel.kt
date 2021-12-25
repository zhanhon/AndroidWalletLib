package com.ramble.ramblewallet.base

import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ramble.ramblewallet.MyApp
import com.ramble.ramblewallet.network.DataService
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.addTo
import com.squareup.moshi.Moshi
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * 时间　: 2021/12/25 15:30
 * 作者　: potato
 * 描述　:
 */
abstract class RxViewModel : AndroidViewModel(MyApp.sInstance) {
    @JvmField
    protected val composite = CompositeDisposable()

//    @Inject
//    lateinit var prefsRepo: PrefsRepo
//
//    @Inject
//    lateinit var db: AppDatabase

    @Inject
    lateinit var moshi: Moshi

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var dataService: DataService

    @Inject
    lateinit var retrofit: Retrofit

    init {
        RxBus.eventObservable()
            .subscribe({ onRxBus(it) }, { it.printStackTrace() })
            .addTo(composite)
    }

    override fun onCleared() {
        super.onCleared()
        composite.clear()
    }

    open fun onRxBus(event: RxBus.Event) {
    }

    companion object {
//        @JvmField
//        val globe = Globe().apply {
//            isNetworkConnected = isNetworkConnected()
//        }

//        @JvmStatic
//        fun notifyGlobeChanged() = RxBus.emitEvent(Pie.EVENT_GLOBE_CHANGED, globe)
//
//        @JvmStatic
//        fun notifyFetchUser() = RxBus.emitEvent(Pie.EVENT_FETCH_USER, Pie.EVENT_FETCH_USER)
//
//        @JvmStatic
//        fun notifyUserBalance() = RxBus.emitEvent(Pie.EVENT_USER_BALANCE, Pie.EVENT_USER_BALANCE)

        @JvmStatic
        fun formatHTML(content: String, title: String = ""): String {
            return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name=viewport content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no">
                  <title>$title</title>
                  <style type="text/css">
                    .htmls { width: auto; overflow: auto; text-align: left; display: block !important; box-sizing: border-box}
                    .htmls * { word-break: break-all; }
                    .htmls img { max-width: 100%; }
                  </style>
                </head>
                <body class="htmls">
                  $content
                </body>
                </html>
            """.trimIndent()
        }


        @JvmField
        val liveEvent = MutableLiveData<RxBus.Event>()
    }
}
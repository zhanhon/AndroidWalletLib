package com.ramble.ramblewallet.network

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.ramble.ramblewallet.MyApp
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 时间　: 2021/12/23 9:46
 * 作者　: potato
 * 描述　:
 */
@Singleton
interface AppComponent {
    fun moshi(): Moshi

    fun okHttpClient(): OkHttpClient

    fun retrofit(): Retrofit

    fun sharedPreferences(): SharedPreferences

//    fun appDatabase(): AppDatabase
//
//    fun prefsRepo(): PrefsRepo

    fun viewModelFactory(): ViewModelProvider.Factory

//    fun appKeeper(): AppKeeper
//
//    fun dataService(): DataService
//
//    fun memoryCache(): SimpleMemoryCache
//
//    fun diskCache(): DiskCache

    fun activityManager(): ActivityManager

    fun inject(it: MyApp)

//    fun inject(activity: MaintainActivity)
//
//    @Component.Factory
//    interface Factory {
//        fun create(@BindsInstance app: MyApp): AppComponent
//    }
}
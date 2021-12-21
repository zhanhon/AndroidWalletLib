package com.ramble.ramblewallet.utils


import androidx.core.util.Pair
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 时间　: 2021/12/20 17:41
 * 作者　: potato
 * 描述　:
 */
object RxBus {
    private val relay by lazy { PublishRelay.create<Any>().toSerialized() }

    fun <T> observable(clazz: Class<T>): Observable<T> {
        return relay.ofType(clazz).observeOn(AndroidSchedulers.mainThread())
    }

    @JvmOverloads
    fun emitEvent(id: Int, data: Any? = null) = relay.accept(Event(id, data))

    fun eventObservable(): Observable<Event> {
        return relay.ofType(Event::class.java).observeOn(AndroidSchedulers.mainThread())
    }

    class Event(id: Int, data: Any?) : Pair<Int, Any>(id, data) {
        @Suppress("UNCHECKED_CAST")
        fun <T> data(): T {
            return second!! as T
        }

        fun id(): Int = first!!
    }
}
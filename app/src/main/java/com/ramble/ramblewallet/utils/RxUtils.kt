package com.ramble.ramblewallet.utils

import android.os.Looper
import io.reactivex.*
import io.reactivex.android.MainThreadDisposable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

/**
 * 时间　: 2021/12/20 17:43
 * 作者　: potato
 * 描述　:
 */
fun isMainThread() = Looper.getMainLooper().thread == Thread.currentThread()

fun verifyMainThread() = MainThreadDisposable.verifyMainThread()

fun verifyWorkThread() {
    if (isMainThread()) {
        throw IllegalStateException("Expected to be called on work thread")
    }
}

fun postToMainThread(runnable: Runnable) {
    AndroidSchedulers.mainThread().scheduleDirect(runnable)
}

fun postToMainThread(runnable: Runnable, delay: Long) {
    AndroidSchedulers.mainThread().scheduleDirect(runnable, delay, TimeUnit.MILLISECONDS)
}

fun threadSleep(millis: Long) {
    try {
        Thread.sleep(millis)
    } catch (ignored: InterruptedException) {
    }
}

fun lenientRxJavaErrorHandler() = RxJavaPlugins.setErrorHandler {}

fun useAsyncMainThreadSchedulers() {
    val asyncMainThreadSchedulers = AndroidSchedulers.from(Looper.getMainLooper(), true)
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { asyncMainThreadSchedulers }
    RxAndroidPlugins.setMainThreadSchedulerHandler { asyncMainThreadSchedulers }
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

fun Disposable.addTo(composite: CompositeDisposable): Disposable {
    composite.add(this)
    return this
}

fun <T> Single<T>.applyIo(): Single<T> {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Single<T>.applyComputation(): Single<T> {
    return this.compose {
        this.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Single<T>.applyNewThread(): Single<T> {
    return this.compose {
        this.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Single<T>.applySingle(): Single<T> {
    return this.compose {
        this.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Single<T>.applyFrom(executor: Executor): Single<T> {
    return this.compose {
        this.subscribeOn(Schedulers.from(executor)).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.applyIo(): Observable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.applyComputation(): Observable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.applyNewThread(): Observable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.applySingle(): Observable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.applyFrom(executor: Executor): Observable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.from(executor)).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Maybe<T>.applyIo(): Maybe<T> {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Maybe<T>.applyComputation(): Maybe<T> {
    return this.compose {
        this.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Maybe<T>.applyNewThread(): Maybe<T> {
    return this.compose {
        this.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Maybe<T>.applySingle(): Maybe<T> {
    return this.compose {
        this.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Maybe<T>.applyFrom(executor: Executor): Maybe<T> {
    return this.compose {
        this.subscribeOn(Schedulers.from(executor)).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Flowable<T>.applyIo(): Flowable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Flowable<T>.applyComputation(): Flowable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Flowable<T>.applyNewThread(): Flowable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Flowable<T>.applySingle(): Flowable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Flowable<T>.applyFrom(executor: Executor): Flowable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.from(executor)).observeOn(AndroidSchedulers.mainThread())
    }
}

fun Completable.applyIo(): Completable {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun Completable.applyComputation(): Completable {
    return this.compose {
        this.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun Completable.applyNewThread(): Completable {
    return this.compose {
        this.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun Completable.applySingle(): Completable {
    return this.compose {
        this.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun Completable.applyFrom(executor: Executor): Completable {
    return this.compose {
        this.subscribeOn(Schedulers.from(executor)).observeOn(AndroidSchedulers.mainThread())
    }
}

@JvmOverloads
fun Runnable.applyIo(composite: CompositeDisposable? = null): Disposable {
    return if (composite == null) {
        Schedulers.io().scheduleDirect(this)
    } else {
        Schedulers.io().scheduleDirect(this).addTo(composite)
    }
}

@JvmOverloads
fun Runnable.applySingle(composite: CompositeDisposable? = null): Disposable {
    return if (composite == null) {
        Schedulers.single().scheduleDirect(this)
    } else {
        Schedulers.single().scheduleDirect(this).addTo(composite)
    }
}

@JvmOverloads
fun Runnable.applyNewThread(composite: CompositeDisposable? = null): Disposable {
    return if (composite == null) {
        Schedulers.newThread().scheduleDirect(this)
    } else {
        Schedulers.newThread().scheduleDirect(this).addTo(composite)
    }
}

@JvmOverloads
fun Runnable.applyComputation(composite: CompositeDisposable? = null): Disposable {
    return if (composite == null) {
        Schedulers.computation().scheduleDirect(this)
    } else {
        Schedulers.computation().scheduleDirect(this).addTo(composite)
    }
}

@JvmOverloads
fun Runnable.applyFrom(executor: Executor, composite: CompositeDisposable? = null): Disposable {
    return if (composite == null) {
        Schedulers.from(executor).scheduleDirect(this)
    } else {
        Schedulers.from(executor).scheduleDirect(this).addTo(composite)
    }
}

fun Runnable.mainThread() {
    AndroidSchedulers.mainThread().scheduleDirect(this)
}

fun Runnable.mainThread(delay: Long) {
    this.mainThread(delay, TimeUnit.MILLISECONDS)
}

fun Runnable.mainThread(delay: Long, unit: TimeUnit) {
    AndroidSchedulers.mainThread().scheduleDirect(this, delay, unit)
}
package com.ramble.ramblewallet.utils

import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 时间　: 2021/12/20 17:43
 * 作者　: potato
 * 描述　:
 */



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

fun <T> Observable<T>.applyIo(): Observable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Maybe<T>.applyIo(): Maybe<T> {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}


fun <T> Flowable<T>.applyIo(): Flowable<T> {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}


fun Completable.applyIo(): Completable {
    return this.compose {
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
package com.ramble.ramblewallet.network

import android.app.Activity
import android.app.Application
import android.os.Bundle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 时间　: 2021/12/21 15:02
 * 作者　: potato
 * 描述　:
 */
@Singleton
class ActivityManager  @Inject constructor() : Application.ActivityLifecycleCallbacks {
    private val activityStack = mutableListOf<Activity>()

    fun getTopActivityOrNull() = activityStack.lastOrNull()

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        activityStack.remove(p0)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        activityStack.add(p0)
    }

    fun clear() {
        activityStack.forEach {
            it.finish()
        }
    }

    fun activityStackCopy(): MutableList<Activity> {
        return mutableListOf<Activity>().also { it.addAll(activityStack) }
    }
}
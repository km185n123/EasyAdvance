package com.paparazziapps.pretamistapp.helper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.util.*

lateinit var ctx      : Context
private val LOG_TAG = "MyApplication"
private val AD_UNIT_ID = "ca-app-pub-4239770697814982/8634079005"

@HiltAndroidApp
class MainApplication : Application(), Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    private var currentActivity: Activity? = null

    private lateinit var appOpenAdManager: AppOpenAdManager
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        turnOffDarkModeInAllApp(resources)
        registerActivityLifecycleCallbacks(this)
        ctx = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let {
            println("App moved to foreground!")
        }
    }



    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        //TODO("Not yet implemented")
    }

    override fun onActivityStarted(activity: Activity) {
        println("activity started")
        if (!appOpenAdManager.isShowingAd) {
            println("currentActivity = $activity")
            currentActivity = activity
        }
    }

    override fun onActivityResumed(p0: Activity) {
        //TODO("Not yet implemented")
    }

    override fun onActivityPaused(p0: Activity) {
        //TODO("Not yet implemented")
    }

    override fun onActivityStopped(p0: Activity) {
        //TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        //TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(p0: Activity) {
        //TODO("Not yet implemented")
    }

    private inner class AppOpenAdManager {

        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0



        /** Check if ad was loaded more than n hours ago. */
        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }


    }

}
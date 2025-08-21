package com.studentsapps.admodule

import android.app.Activity
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    private val cache: InterstitialAdCache
) {
    private val interstitialId = "ca-app-pub-3940256099942544/1033173712"

    fun preload(context: Context) {
        cache.loadAd(context, interstitialId)
    }

    fun showInterstitial(activity: Activity, onAdDismissed: (() -> Unit)? = null) {
        cache.showAdIfAvailable(activity, interstitialId, onAdDismissed)
    }
}
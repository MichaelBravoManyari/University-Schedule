package com.studentsapps.admodule

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdCache @Inject constructor() {

    private var interstitialAd: AtomicReference<InterstitialAd?> = AtomicReference(null)
    private var lastLoadedTime: Long = 0L
    private var lastShownTime: Long = 0L
    private var isLoading = false

    private val adExpiration = 60 * 60 * 1000
    private val adCooldown = 2 * 60 * 1000

    fun loadAd(context: Context, adUnitId: String) {
        if (isLoading) return
        if (System.currentTimeMillis() - lastLoadedTime < adExpiration && interstitialAd.get() != null) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd.set(ad)
                lastLoadedTime = System.currentTimeMillis()
                isLoading = false
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd.set(null)
                isLoading = false
            }
        })
    }

    fun showAdIfAvailable(activity: Activity, adUnitId: String, onAdDismissed: (() -> Unit)? = null) {
        val ad = interstitialAd.get()

        if (ad != null && canShowAd()) {
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onAdDismissed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    onAdDismissed?.invoke()
                }
            }

            ad.show(activity)
            lastShownTime = System.currentTimeMillis()
            interstitialAd.set(null)
            loadAd(activity.applicationContext, adUnitId)
        } else {
            loadAd(activity.applicationContext, adUnitId)
            onAdDismissed?.invoke()
        }
    }

    private fun canShowAd(): Boolean {
        return System.currentTimeMillis() - lastShownTime >= adCooldown
    }
}
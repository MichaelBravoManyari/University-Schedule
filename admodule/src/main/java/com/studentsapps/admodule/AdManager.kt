package com.studentsapps.admodule

import android.app.Activity
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager
    @Inject
    constructor(
        private val cache: InterstitialAdCache,
    ) {
        private val interstitialId = BuildConfig.ADMOB_INTERSTITIAL_ID

        fun preload(context: Context) {
            cache.loadAd(context, interstitialId)
        }

        fun showInterstitial(
            activity: Activity,
            onAdDismissed: (() -> Unit)? = null,
        ) {
            cache.showAdIfAvailable(activity, interstitialId, onAdDismissed)
        }
    }

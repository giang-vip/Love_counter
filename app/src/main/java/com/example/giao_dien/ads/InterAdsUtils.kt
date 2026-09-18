package com.example.giao_dien.ads

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.interstitial.InterstitialAdSplashConfig
import com.ads.admob.helper.interstitial.InterstitialAdSplashHelper
import com.ads.admob.helper.interstitial.params.InterstitialAdParam
import com.ads.admob.listener.InterstitialAdCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError

object InterAdsUtils {

    fun showSplashAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        action: () -> Unit
    ) {
        val splashAdConfig = InterstitialAdSplashConfig(
            idAds = idAds,
            adPlacement = adPlacement,
            canShowAds = FirebaseConfigManager.getInstance().adConfig.configAds && isEnable,
            canReloadAds = true,
            reloadIfFirstFail = true,
            timeOut = 10_000L,
            timeDelay = 1_000L,
            showReady = true
        )

        val helper = InterstitialAdSplashHelper(activity, lifecycleOwner, splashAdConfig)

        helper.registerAdListener(object : InterstitialAdCallback {
            override fun onNextAction() {
                action.invoke()
            }

            override fun onAdClose() {
                action.invoke()
            }

            override fun onInterstitialShow() {}
            override fun onAdLoaded(data: ContentAd) {}
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
            override fun onAdClicked() {}
            override fun onAdImpression() {}
            override fun onAdFailedToShow(adError: AdError) {}
        })

        helper.requestAds(InterstitialAdParam.Request)
    }
}

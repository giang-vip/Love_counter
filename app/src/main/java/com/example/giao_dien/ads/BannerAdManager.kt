package com.example.giao_dien.ads

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class BannerAdManager {

    private var adView: AdView? = null

    /**
     * Tải và tự động chèn Adaptive Banner Ad vào Container truyền vào
     */
    fun loadBannerAd(activity: Activity, container: ViewGroup) {
        if (!AdConfig.isAdsEnabled) {
            container.visibility = View.GONE
            return
        }

        adView = AdView(activity).apply {
            adUnitId = AdConfig.remoteBannerId
            setAdSize(getAdSize(activity, container))
        }

        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                container.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                container.visibility = View.GONE
            }
        }

        container.removeAllViews()
        container.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    /**
     * Tính toán AdSize Adaptive phù hợp với độ rộng thực tế của thiết bị
     */
    private fun getAdSize(activity: Activity, container: ViewGroup): AdSize {
        val displayMetrics = activity.resources.displayMetrics
        val density = displayMetrics.density

        var adWidthPixels = container.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = displayMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    /**
     * Hủy và giải phóng bộ nhớ Banner Ad (Gắn vào onDestroy của Activity)
     */
    fun destroyAd() {
        adView?.destroy()
        adView = null
    }
}

package com.example.giao_dien.ads

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.adnative.NativeAdConfig
import com.ads.admob.helper.adnative.NativeAdHelper
import com.ads.admob.helper.adnative.params.NativeAdParam
import com.ads.admob.listener.NativeAdCallback
import com.example.giao_dien.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.LoadAdError

object NativeAdsUtils {

    fun loadAndShowNativeAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        container: ViewGroup,
        layoutResId: Int = R.layout.layout_native_ad_language
    ): NativeAdHelper? {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) {
            container.visibility = android.view.View.GONE
            return null
        }

        container.removeAllViews()

        // Thêm Shimmer View
        val shimmerView = LayoutInflater.from(activity).inflate(R.layout.layout_native_ad_shimmer, container, false) as ShimmerFrameLayout
        container.addView(shimmerView)

        // Thêm Native Content View
        val nativeContentView = FrameLayout(activity)
        nativeContentView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        container.addView(nativeContentView)

        val config = NativeAdConfig(
            idAds = idAds,
            canShowAds = true,
            canReloadAds = true,
            layoutId = layoutResId,
            adPlacement = adPlacement
        )

        val helper = NativeAdHelper(activity, lifecycleOwner, config)
        helper.setShimmerLayoutView(shimmerView)
        helper.setNativeContentView(nativeContentView)

        helper.registerAdListener(object : NativeAdCallback {
            override fun populateNativeAd() {
                // Sẽ được gọi khi thư viện đổ data vào view
            }

            override fun onAdLoaded(data: ContentAd) {}

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                container.visibility = android.view.View.GONE
            }

            override fun onAdFailedToShow(adError: com.google.android.gms.ads.AdError) {}
            override fun onAdClicked() {}
            override fun onAdImpression() {}
        })

        helper.requestAds(NativeAdParam.Request.create())
        return helper
    }
}

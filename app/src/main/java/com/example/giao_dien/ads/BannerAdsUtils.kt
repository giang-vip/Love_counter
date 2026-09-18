package com.example.giao_dien.ads

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.listener.BannerAdCallBack
import com.example.giao_dien.R
import com.google.android.gms.ads.LoadAdError

object BannerAdsUtils {

    fun loadBanner(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        container: FrameLayout,
        useInlineAdaptive: Boolean = false
    ): BannerAdHelper? {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) {
            container.visibility = View.GONE
            return null
        }

        // QUAN TRỌNG: Thư viện yêu cầu Container phải chứa sẵn layout có ID @id/shimmer_container_banner
        // Nếu không có, hàm handleShowAds trong BannerAdHelper sẽ không chạy!
        container.removeAllViews()
        val bannerLayout = LayoutInflater.from(activity).inflate(
            com.ads.admob.R.layout.layout_banner_control, 
            container, 
            false
        )
        container.addView(bannerLayout)

        val config = BannerAdConfig(
            idAds = idAds,
            canShowAds = true,
            canReloadAds = true,
            adPlacement = adPlacement
        )

        val helper = BannerAdHelper(activity, lifecycleOwner, config)
        
        helper.registerAdListener(object : BannerAdCallBack {
            override fun onAdLoaded(data: ContentAd) {
                container.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // container.visibility = View.GONE
            }

            override fun onAdFailedToShow(adError: com.google.android.gms.ads.AdError) {}
            override fun onAdClicked() {}
            override fun onAdImpression() {}
        })

        // Giao cái container (đã có shimmer) cho Helper
        helper.setBannerContentView(container)
        helper.requestAds(BannerAdParam.Request.create())

        return helper
    }
}

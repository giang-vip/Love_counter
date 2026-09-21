package com.app.love_counter.ads

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.admob.BannerCollapsibleGravity
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.listener.BannerAdCallBack
import com.google.android.gms.ads.LoadAdError

/**
 * UTILS QUẢN LÝ QUẢNG CÁO BIỂU NGỮ (BANNER ADS)
 * Banner là thanh quảng cáo nằm cố định ở một góc (thường là đáy) màn hình. (Ví dụ: HomeActivity).
 */
class BannerAdsUtils private constructor() {

    private val processLifecycleOwner = ProcessLifecycleOwner.get()
    
    // Lưu trữ đối tượng Banner theo Placement để quản lý dễ dàng
    private val bannerAdsMap = HashMap<String, BannerAdHelper>()

    init {
        // Tự động giải phóng RAM khi App bị đóng
        processLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                bannerAdsMap.values.forEach { it.cancel() }
                bannerAdsMap.clear()
            }
        })
    }

    companion object {
        @Volatile
        private var instance: BannerAdsUtils? = null

        fun getInstance(): BannerAdsUtils {
            return instance ?: synchronized(this) {
                instance ?: BannerAdsUtils().also { instance = it }
            }
        }
    }

    /**
     * Tải và nhúng thẳng Banner vào Container.
     */
    fun initBannerHome(
        activity: Activity,
        lifecycleOwnerActivity: LifecycleOwner,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        container: FrameLayout
    ) {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) {
            container.visibility = View.GONE
            return
        }

        /* 
         * TRICK (MẸO): Bypass lỗi của thư viện.
         * Thư viện yêu cầu Container bắt buộc phải có sẵn layout chứa ID "@id/shimmer_container_banner".
         * Vì vậy, chúng ta bơm (inflate) một layout rỗng chuẩn của thư viện vào Container trước khi gọi Ads.
         */
        container.removeAllViews()
        val bannerLayout = LayoutInflater.from(activity).inflate(
            com.ads.admob.R.layout.layout_banner_control, 
            container, 
            false
        )
        container.addView(bannerLayout)

        val config = BannerAdConfig(
            idAds = idAds,
            canShowAds = FirebaseConfigManager.getInstance().adConfig.configAds && isEnable,
            canReloadAds = true,
            adPlacement = adPlacement,
            reloadIfFirstFail = true 
        ).apply {
            collapsibleGravity = BannerCollapsibleGravity.BOTTOM
        }

        val bannerAdHelper = BannerAdHelper(activity, lifecycleOwnerActivity, config)
        
        bannerAdHelper.registerAdListener(object : BannerAdCallBack {
            override fun onAdLoaded(data: ContentAd) {
                container.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Giữ lại view hoặc ẩn tuỳ UX, ở đây ẩn đi nếu lỗi
                // container.visibility = View.GONE 
            }

            override fun onAdFailedToShow(adError: com.google.android.gms.ads.AdError) {}
            override fun onAdClicked() {}
            override fun onAdImpression() {}
        })

        // Ném nguyên cái container vào cho Helper, nó sẽ tự lôi cái Shimmer ra chạy, sau đó nhét Banner đè lên
        bannerAdHelper.setBannerContentView(container)
        bannerAdHelper.requestAds(BannerAdParam.Request.create())
        bannerAdsMap[adPlacement] = bannerAdHelper
    }

    /**
     * Xóa hết quảng cáo khỏi RAM khi người dùng mua VIP
     */
    fun cancelAllAds(isPurchased: Boolean) {
        bannerAdsMap.values.forEach {
            it.cancelRequestAndShowAllAds(isPurchased)
            it.cancel()
        }
        bannerAdsMap.clear()
    }
}

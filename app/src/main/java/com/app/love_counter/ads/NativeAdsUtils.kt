package com.app.love_counter.ads

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.AdOptionVisibility
import com.ads.admob.helper.adnative.NativeAdConfig
import com.ads.admob.helper.adnative.NativeAdHelper
import com.ads.admob.helper.adnative.params.NativeAdParam
import com.app.love_counter.R
import com.facebook.shimmer.ShimmerFrameLayout

/**
 * UTILS QUẢN LÝ QUẢNG CÁO TỰ NHIÊN (NATIVE ADS)
 * Chuyển sang cơ chế Tải Mồi (Preload) - Hiện Sau để UX mượt mà nhất (chuẩn MetalDetector).
 */
class NativeAdsUtils private constructor() {

    private val processLifecycleOwner = ProcessLifecycleOwner.get()

    // Quản lý tất cả Native Ads theo AdPlacement (không hardcode từng biến như cũ)
    private val nativeAdsMap = HashMap<String, NativeAdHelper>()

    init {
        // Tự động dọn rác (Clear RAM) khi App bị đóng
        processLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                // Xóa và giải phóng tất cả Native Ads khi app bị diệt
                nativeAdsMap.values.forEach { it.cancel() }
                nativeAdsMap.clear()
            }
        })
    }

    companion object {
        @Volatile
        private var instance: NativeAdsUtils? = null

        fun getInstance(): NativeAdsUtils {
            return instance ?: synchronized(this) {
                instance ?: NativeAdsUtils().also { instance = it }
            }
        }
    }

    /**
     * TẢI MỒI (PRELOAD) TRƯỚC: Gọi ở màn hình trước đó.
     * Mồi quảng cáo nằm im trong RAM chờ được gọi.
     */
    fun loadNativeAds(
        activity: Activity,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        layoutResId: Int = R.layout.layout_native_ad_language
    ) {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) return

        val nativeAdConfig = NativeAdConfig(
            idAds = idAds,
            canShowAds = true,
            canReloadAds = false, // Không tự động tải lại để tránh tốn tài nguyên chạy ngầm
            layoutId = layoutResId,
            adPlacement = adPlacement,
        )

        val helper = NativeAdHelper(activity, processLifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        
        // Lưu helper vào map theo placement
        nativeAdsMap[adPlacement] = helper
        helper.requestAds(NativeAdParam.Request.create())
    }

    /**
     * HIỂN THỊ QUẢNG CÁO ĐÃ TẢI MỒI: Bê từ RAM ra giao diện ngay lập tức.
     */
    fun showNativeAds(
        container: ViewGroup,
        adPlacement: String
    ) {
        val helper = nativeAdsMap[adPlacement]
        if (helper == null) {
            container.visibility = android.view.View.GONE
            return
        }

        container.removeAllViews()
        val shimmerView = LayoutInflater.from(container.context).inflate(R.layout.layout_native_ad_shimmer, container, false) as ShimmerFrameLayout
        val nativeContentView = FrameLayout(container.context)
        container.addView(shimmerView)
        container.addView(nativeContentView)

        helper.setShimmerLayoutView(shimmerView)
        helper.setNativeContentView(nativeContentView)
        
        // -------------------------------------------------------------------
        // [CƠ CHẾ BẮT LỖI MẠNG CHO NATIVE ADS]
        // Nếu không có mạng hoặc mạng quá yếu, quảng cáo không thể tải.
        // Google SDK sẽ gọi hàm onAdFailedToLoad. Ta phải lắng nghe sự kiện này
        // để lập tức ẨN KHUNG QUẢNG CÁO (View.GONE) đi. Giúp giao diện App tự co lại
        // mượt mà, không bị hở một khoảng trắng lớn hay bị chớp nháy Shimmer vô tận.
        // -------------------------------------------------------------------
        helper.registerAdListener(object : com.ads.admob.listener.NativeAdCallback {
            override fun populateNativeAd() {}
            override fun onAdLoaded(data: com.ads.admob.data.ContentAd) {}
            override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                container.visibility = android.view.View.GONE // Ẩn ngay lập tức
            }
            override fun onAdFailedToShow(adError: com.google.android.gms.ads.AdError) {}
            override fun onAdClicked() {}
            override fun onAdImpression() {}
        })

        helper.requestAds(NativeAdParam.Request.create())
    }

    /**
     * Xóa hết quảng cáo khỏi RAM khi người dùng mua VIP
     */
    fun cancelAllAds(isPurchased: Boolean) {
        nativeAdsMap.values.forEach {
            it.cancelRequestAndShowAllAds(isPurchased)
            it.cancel()
        }
        nativeAdsMap.clear()
    }
}

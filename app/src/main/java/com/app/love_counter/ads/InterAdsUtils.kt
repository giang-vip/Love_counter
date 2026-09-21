package com.app.love_counter.ads

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.interstitial.InterstitialAdConfig
import com.ads.admob.helper.interstitial.InterstitialAdsHelper
import com.ads.admob.helper.interstitial.InterstitialAdSplashConfig
import com.ads.admob.helper.interstitial.InterstitialAdSplashHelper
import com.ads.admob.helper.interstitial.params.InterstitialAdParam
import com.ads.admob.listener.InterstitialAdCallback
import com.ads.admob.listener.InterstitialAdRequestCallBack
import com.ads.admob.listener.InterstitialAdShowCallBack
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError

/**
 * UTILS QUẢN LÝ QUẢNG CÁO TOÀN MÀN HÌNH (INTERSTITIAL)
 * Loại quảng cáo này che kín toàn bộ màn hình, thường xuất hiện ở Splash hoặc khi chuyển đổi các màn hình lớn.
 */
class InterAdsUtils private constructor() {

    private var lastInterShowTime = 0L
    var INTERVAL_TIME = 30_000L // Khoảng thời gian bắt buộc giữa 2 lần hiện quảng cáo (30 giây để chống spam)

    companion object {
        @Volatile
        private var instance: InterAdsUtils? = null

        fun getInstance(): InterAdsUtils {
            return instance ?: synchronized(this) {
                instance ?: InterAdsUtils().also { instance = it }
            }
        }
    }

    /**
     * Hàm gọi hiển thị quảng cáo Interstitial ở màn hình Splash.
     * @param action Hàm lambda chứa luồng code tiếp theo (Ví dụ: Chuyển màn hình). Nó được kích hoạt khi quảng cáo kết thúc hoặc lỗi.
     */
    fun showSplashAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        action: () -> Unit
    ) {
        // 1. TẠO LUẬT (CONFIG): Cài đặt thời gian chờ tối đa (timeout) và cờ bật/tắt
        val splashAdConfig = InterstitialAdSplashConfig(
            idAds = idAds,
            adPlacement = adPlacement,
            canShowAds = FirebaseConfigManager.getInstance().adConfig.configAds && isEnable,
            canReloadAds = true,
            reloadIfFirstFail = true,
            timeOut = 10_000L, // Tối đa 10s, quá giờ sẽ bỏ qua để user không bị kẹt
            timeDelay = 1_000L,
            showReady = true
        )

        // 2. KHỞI TẠO TRỢ THỦ: Vì Splash chỉ hiện 1 lần lúc mở app, nên dùng `val helper` tạo mới 1 lần là được, không cần lưu vào getInstance() để tiết kiệm RAM.
        val helper = InterstitialAdSplashHelper(activity, lifecycleOwner, splashAdConfig)

        // 3. ĐẶT ỐNG NGHE (CALLBACK): Lắng nghe kết quả từ thư viện
        helper.registerAdListener(object : InterstitialAdCallback {
            // onNextAction: Xảy ra khi mạng lỗi, hết 10s timeout, hoặc Firebase tắt cờ quảng cáo
            override fun onNextAction() {
                action.invoke() // Chạy code tiếp tục vòng đời App (vào màn Home/Intro)
            }

            // onAdClose: Xảy ra khi quảng cáo đã hiện lên thành công và user bấm [X] tắt nó đi
            override fun onAdClose() {
                action.invoke() // Chạy code tiếp tục vòng đời App
            }

            // Các sự kiện khác (Không bắt buộc xử lý để điều hướng)
            override fun onInterstitialShow() {}
            override fun onAdLoaded(data: ContentAd) {}
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
            override fun onAdClicked() {}
            override fun onAdImpression() {}
            override fun onAdFailedToShow(adError: AdError) {}
        })

        // 4. RA LỆNH THỰC THI: Bắt đầu tải và hiện quảng cáo
        helper.requestAds(InterstitialAdParam.Request)
    }

    /**
     * TẢI MỒI (PRELOAD) QUẢNG CÁO
     * Gọi hàm này ở onCreate() của Activity trước đó để tải sẵn quảng cáo vào RAM.
     */
    fun loadInterAds(
        activity: Activity,
        idAdsPriority: String? = null,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        isLoadAndShow: Boolean = false,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) return

        InterstitialAdsHelper.getInstance(adPlacement).setInterstitialAdConfig(
            InterstitialAdConfig(
                idAdsPriority = idAdsPriority,
                idAds = idAds,
                canShowAds = true,
                canReloadAds = false,
                adPlacement = adPlacement,
                reloadIfFirstFail = false,
            )
        )
        loadInter(activity, adPlacement, isLoadAndShow, onAdLoaded, onAdLoadFail)
    }

    private fun loadInter(
        activity: Activity, adPlacement: String, isLoadAndShow: Boolean = false,
        onAdLoaded: (() -> Unit)?, onAdLoadFail: (() -> Unit)?
    ) {
        InterstitialAdsHelper.getInstance(adPlacement)
            .requestInterAds(activity, isLoadAndShow, object : InterstitialAdRequestCallBack {
                override fun onAdLoaded(data: ContentAd) { onAdLoaded?.invoke() }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) { onAdLoadFail?.invoke() }
            })
    }

    /**
     * HIỂN THỊ QUẢNG CÁO ĐÃ TẢI MỒI (CÓ CHỐNG SPAM)
     * Gọi hàm này khi người dùng bấm nút chuyển màn hình.
     */
    fun showInterAdsV2(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        adPlacement: String,
        isEnable: Boolean,
        isReloadWhenClose: Boolean,
        action: () -> Unit
    ) {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) {
            action.invoke()
            return
        }

        // KIỂM TRA CHỐNG SPAM
        val timeDiff = System.currentTimeMillis() - lastInterShowTime
        if (timeDiff < INTERVAL_TIME) {
            // Nếu thời gian từ lần hiện trước đến nay nhỏ hơn 30s -> BỎ QUA QUẢNG CÁO
            action.invoke()
            return
        }

        InterstitialAdsHelper.getInstance(adPlacement)
            .forceShowInterstitial(activity, lifecycleOwner, object : InterstitialAdShowCallBack {
                override fun onAdClose() {
                    lastInterShowTime = System.currentTimeMillis() // Cập nhật lại mốc thời gian vừa đóng ads
                    if (isReloadWhenClose) loadInter(activity, adPlacement, false, null, null) // Tự nạp lại đạn cho lần sau
                    action.invoke() // Chuyển màn hình
                }
                override fun onAdFailedToShow(adError: AdError) {
                    if (isReloadWhenClose) loadInter(activity, adPlacement, false, null, null)
                    action.invoke() // Lỗi không hiện được thì cho qua luôn
                }
                override fun onInterstitialShow() {
                    lastInterShowTime = System.currentTimeMillis()
                }
                override fun onNextAction() {
                    action.invoke() // Xử lý các case Timeout hoặc lỗi mạng
                }
                override fun onAdClicked() {}
                override fun onAdImpression() {}
            })
    }

    /**
     * XÓA SẠCH RAM KHI MUA VIP
     * Gọi hàm này khi người dùng thanh toán mua gói No-Ads thành công.
     */
    fun cancelAllAds(isPurchased: Boolean) {
        InterstitialAdsHelper.getInstance(AdPlacement.INTER_SPLASH).cancelRequestAndShowAllAds(isPurchased)
        // Nếu có nhiều placement hơn thì copy dòng trên và đổi tên placement.
    }
}

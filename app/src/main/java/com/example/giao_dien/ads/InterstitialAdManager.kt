package com.example.giao_dien.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager private constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    /**
     * Tải trước (preload) Interstitial Ad vào bộ nhớ đệm
     */
    fun loadAd(context: Context) {
        // Nếu cờ Bật/Tắt quảng cáo từ xa Firebase Remote Config tắt ➔ Ngừng tải
        if (!AdConfig.isAdsEnabled) return
        if (isLoading || isAdAvailable()) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        // Sử dụng Mã ID từ file cấu hình tập trung AdConfig
        InterstitialAd.load(
            context,
            AdConfig.remoteInterstitialId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    /**
     * Kiểm tra quảng cáo đã tải xong chưa
     */
    fun isAdAvailable(): Boolean {
        return AdConfig.isAdsEnabled && interstitialAd != null
    }

    /**
     * Hiển thị quảng cáo chèn giữa và gọi callback khi người dùng bấm nút [X] đóng ad (hoặc ad lỗi)
     */
    fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (AdConfig.isAdsEnabled && interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onAdDismissed() // Người dùng tắt ad ➔ Cho phép chuyển vào app
                    loadAd(activity) // Tải sẵn ad mới cho lần dùng sau
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onAdDismissed()
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    // Quảng cáo đang được hiển thị toàn màn hình
                }
            }
            interstitialAd?.show(activity)
        } else {
            // Nếu ad chưa sẵn sàng hoặc bị tắt ➔ Trực tiếp cho phép chuyển vào app không bắt chờ
            onAdDismissed()
            if (AdConfig.isAdsEnabled) {
                loadAd(activity)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: InterstitialAdManager? = null

        fun getInstance(): InterstitialAdManager {
            return instance ?: synchronized(this) {
                instance ?: InterstitialAdManager().also { instance = it }
            }
        }
    }
}

package com.example.giao_dien.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdManager private constructor() {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    /**
     * Tải trước (preload) Rewarded Ad vào bộ nhớ đệm
     */
    fun loadAd(context: Context) {
        if (!AdConfig.isAdsEnabled) return
        if (isLoading || isAdAvailable()) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            AdConfig.remoteRewardedId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isLoading = false
                }
            }
        )
    }

    /**
     * Kiểm tra xem Quảng cáo thưởng đã tải xong chưa
     */
    fun isAdAvailable(): Boolean {
        return AdConfig.isAdsEnabled && rewardedAd != null
    }

    /**
     * Hiển thị Rewarded Video Ad và gọi callback khi người dùng xem HẾT video nhận thưởng
     */
    fun showAd(activity: Activity, onRewardEarned: () -> Unit, onAdDismissed: () -> Unit) {
        if (rewardedAd != null) {
            var isUserEarnedReward = false

            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    if (isUserEarnedReward) {
                        onRewardEarned() // Người dùng đã xem HẾT 30s video ➔ Trao thưởng!
                    } else {
                        onAdDismissed() // Tắt ad giữa chừng ➔ Ở lại màn nhập liệu
                    }
                    loadAd(activity) // Preload ad cho lần sau
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    onAdDismissed()
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    // Video quảng cáo bắt đầu phát
                }
            }

            // Gọi show kèm OnUserEarnedRewardListener
            rewardedAd?.show(activity) { _ ->
                isUserEarnedReward = true
            }
        } else {
            onAdDismissed()
            loadAd(activity)
        }
    }

    companion object {
        @Volatile
        private var instance: RewardedAdManager? = null

        fun getInstance(): RewardedAdManager {
            return instance ?: synchronized(this) {
                instance ?: RewardedAdManager().also { instance = it }
            }
        }
    }
}

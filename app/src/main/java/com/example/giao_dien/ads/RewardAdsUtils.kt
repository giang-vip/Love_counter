package com.example.giao_dien.ads

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.AdmobManager
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.helper.reward.RewardAdConfig
import com.ads.admob.helper.reward.RewardAdHelper
import com.ads.admob.listener.RewardAdRequestCallBack
import com.ads.admob.listener.RewardAdCallBack
import com.ads.admob.data.ContentAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem

object RewardAdsUtils {

    /**
     * Tải trước quảng cáo Rewarded Video ngầm ở dưới nền.
     */
    fun loadRewardAds(
        activity: Activity,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean
    ) {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) return

        val config = RewardAdConfig(
            idAds = idAds,
            adPlacement = adPlacement,
            canShowAds = true,
            canReloadAds = true
        )

        val helper = RewardAdHelper.getInstance(adPlacement)
        helper.setRewardAdConfig(config)

        helper.requestRewardAds(
            activity = activity,
            isLoadAndShow = false,
            rewardAdRequestCallBack = object : RewardAdRequestCallBack {
                override fun onAdLoaded(data: ContentAd) {}
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
            }
        )
    }

    /**
     * Hiển thị quảng cáo Rewarded Video.
     * Vì class RewardAdHelper trong thư viện bị lỗi để trống phần onUserEarnedReward,
     * nên chúng ta sẽ gọi trực tiếp qua AdmobFactory để xử lý nhận thưởng chuẩn xác.
     */
    fun showRewardAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        adPlacement: String,
        isEnable: Boolean,
        onEarned: () -> Unit,
        onNextAction: () -> Unit
    ) {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds || !isEnable) {
            onEarned.invoke()
            return
        }

        val helper = RewardAdHelper.getInstance(adPlacement)
        val adValue = helper.rewardAdValue

        if (adValue != null) {
            AdmobManager.adsShowFullScreen()
            
            AdmobFactory.INSTANCE.showRewardAd(
                activity,
                adValue,
                adPlacement,
                object : RewardAdCallBack {
                    private var isEarned = false

                    override fun onUserEarnedReward(rewardItem: RewardItem?) {
                        isEarned = true
                        onEarned.invoke()
                    }

                    override fun onAdClose() {
                        AdmobManager.adsFullScreenDismiss()
                        if (!isEarned) {
                            onNextAction.invoke()
                        }
                    }

                    override fun onRewardShow() {}
                    override fun onAdLoaded(data: ContentAd) {}
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
                    override fun onAdClicked() {}
                    override fun onAdImpression() {}
                    override fun onAdFailedToShow(adError: AdError) {
                        AdmobManager.adsFullScreenDismiss()
                        onEarned.invoke() // Lỗi show thì cho qua luôn
                    }
                }
            )
        } else {
            // Nếu chưa load kịp thì cho qua để user không bị kẹt
            onEarned.invoke()
        }
    }
}

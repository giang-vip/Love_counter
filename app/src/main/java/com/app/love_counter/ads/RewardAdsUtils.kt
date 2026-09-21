package com.app.love_counter.ads

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

/**
 * UTILS QUẢN LÝ QUẢNG CÁO CÓ THƯỞNG (REWARDED ADS)
 * Yêu cầu user xem hết video 30s để đổi lấy một phần thưởng (Ví dụ: Cho phép xem kết quả bói toán).
 */
class RewardAdsUtils private constructor() {

    companion object {
        @Volatile
        private var instance: RewardAdsUtils? = null

        fun getInstance(): RewardAdsUtils {
            return instance ?: synchronized(this) {
                instance ?: RewardAdsUtils().also { instance = it }
            }
        }
    }

    /**
     * TẢI TRƯỚC (PRELOAD) QUẢNG CÁO
     * Đặt hàm này ở `onCreate` của màn hình. Nó tải video ngầm dưới nền để khi user bấm nút sẽ có ngay video xem.
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
            isLoadAndShow = false, // false = Chỉ tải, không được hiện
            rewardAdRequestCallBack = object : RewardAdRequestCallBack {
                override fun onAdLoaded(data: ContentAd) {}
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
            }
        )
    }

    /**
     * HIỂN THỊ QUẢNG CÁO CÓ THƯỞNG
     * Đặt hàm này ở nút "Bấm để xem kết quả".
     * @param onEarned Hành động xảy ra khi user ĐÃ XEM HẾT 30s.
     * @param onNextAction Hành động xảy ra khi user BẤM TẮT GIỮA CHỪNG.
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
        val adValue = helper.rewardAdValue // Lấy video đã preload lúc nãy ra

        if (adValue != null) {
            AdmobManager.adsShowFullScreen()
            
            /* TRICK: Gọi thẳng vào `AdmobFactory.INSTANCE` thay vì dùng Helper.
             * Lý do: Hàm onUserEarnedReward bên trong thư viện RewardAdHelper bị anh Lead... code sót, 
             * nó không chịu truyền tín hiệu về App, dẫn đến user xem xong mà app vẫn bị treo.
             * Bypass bằng cách giao tiếp trực tiếp với "Sếp tổng" AdmobFactory.
             */
            AdmobFactory.INSTANCE.showRewardAd(
                activity,
                adValue,
                adPlacement,
                object : RewardAdCallBack {
                    private var isEarned = false

                    // User ngoan ngoãn xem hết 30s -> Nhận thưởng!
                    override fun onUserEarnedReward(rewardItem: RewardItem?) {
                        isEarned = true
                        onEarned.invoke()
                    }

                    // User bấm [X] tắt quảng cáo
                    override fun onAdClose() {
                        AdmobManager.adsFullScreenDismiss()
                        if (!isEarned) {
                            // Xem chưa xong mà dám tắt -> Phạt, gọi onNextAction để cấm xem kết quả bói
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
                        onEarned.invoke() // Lỗi show thì thôi tha cho đi qua luôn
                    }
                }
            )
        } else {
            // Nhấn nút xem rồi mà video chưa load xong -> Tội đồ do mạng yếu, thôi tha cho qua luôn
            onEarned.invoke()
        }
    }
}

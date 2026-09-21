package com.app.love_counter.ads

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.app.love_counter.BuildConfig

/**
 * Data class cấu hình trạng thái bật/tắt của từng vị trí quảng cáo.
 * Được ánh xạ trực tiếp với cấu trúc JSON trên Firebase Remote Config.
 */
data class AdConfig(
    @SerializedName("config_ads") @Expose var configAds: Boolean = true,
    @SerializedName("inter_splash") @Expose var interSplash: Boolean = true,
    @SerializedName("inter_feature") @Expose var interFeature: Boolean = true,
    @SerializedName("native_language") @Expose var nativeLanguage: Boolean = true,
    @SerializedName("native_intro") @Expose var nativeIntro: Boolean = true,
    @SerializedName("native_permission") @Expose var nativePermission: Boolean = true,
    @SerializedName("banner_home") @Expose var bannerHome: Boolean = true,
    @SerializedName("app_open_resume") @Expose var appOpenResume: Boolean = true,
    @SerializedName("reward_name_test") @Expose var rewardNameTest: Boolean = true,
    @SerializedName("reward_horoscope") @Expose var rewardHoroscope: Boolean = true
) {
    // Các ID mặc định cho môi trường test (nếu cấu hình Remote Config lấy ID không thành công sẽ fallback dùng các id này)
    companion object {
        const val INTERSTITIAL_AD_UNIT_ID = BuildConfig.inter_id
        const val NATIVE_AD_UNIT_ID = BuildConfig.native_id
        const val BANNER_AD_UNIT_ID = BuildConfig.banner_id
        const val APP_OPEN_AD_UNIT_ID = BuildConfig.app_open_id
        const val REWARDED_AD_UNIT_ID = BuildConfig.reward_id
        
        var remoteInterstitialId: String = INTERSTITIAL_AD_UNIT_ID
        var remoteNativeId: String = NATIVE_AD_UNIT_ID
        var remoteBannerId: String = BANNER_AD_UNIT_ID
        var remoteAppOpenId: String = APP_OPEN_AD_UNIT_ID
        var remoteRewardedId: String = REWARDED_AD_UNIT_ID
    }
}

package com.example.giao_dien.utils

import com.example.giao_dien.ads.AdConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object FirebaseRemoteConfigHelper {

    fun initAndFetchRemoteConfig() {
        try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()

            // 1. Cấu hình thời gian cập nhật tối thiểu (0s khi dev/test để fetch ngay lập tức)
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)

            // 2. Đọc NGAY LẬP TỨC giá trị đã lưu đệm (Cache) từ lần fetch trước
            if (remoteConfig.all.containsKey("is_ads_enabled")) {
                AdConfig.isAdsEnabled = remoteConfig.getBoolean("is_ads_enabled")
            }

            // Thiết lập giá trị mặc định phòng trường hợp mất mạng
            val defaults = mapOf(
                "is_ads_enabled" to true,
                "interstitial_ad_id" to AdConfig.INTERSTITIAL_AD_UNIT_ID,
                "native_ad_id" to AdConfig.NATIVE_AD_UNIT_ID,
                "banner_ad_id" to AdConfig.BANNER_AD_UNIT_ID,
                "rewarded_ad_id" to AdConfig.REWARDED_AD_UNIT_ID,
                "app_open_ad_id" to AdConfig.APP_OPEN_AD_UNIT_ID
            )
            remoteConfig.setDefaultsAsync(defaults)

            // 3. Tải dữ liệu cấu hình mới nhất từ trang web Firebase Console
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    AdConfig.isAdsEnabled = remoteConfig.getBoolean("is_ads_enabled")

                    val remoteInterstitial = remoteConfig.getString("interstitial_ad_id")
                    if (remoteInterstitial.isNotEmpty()) AdConfig.remoteInterstitialId = remoteInterstitial

                    val remoteNative = remoteConfig.getString("native_ad_id")
                    if (remoteNative.isNotEmpty()) AdConfig.remoteNativeId = remoteNative

                    val remoteBanner = remoteConfig.getString("banner_ad_id")
                    if (remoteBanner.isNotEmpty()) AdConfig.remoteBannerId = remoteBanner

                    val remoteRewarded = remoteConfig.getString("rewarded_ad_id")
                    if (remoteRewarded.isNotEmpty()) AdConfig.remoteRewardedId = remoteRewarded

                    val remoteAppOpen = remoteConfig.getString("app_open_ad_id")
                    if (remoteAppOpen.isNotEmpty()) AdConfig.remoteAppOpenId = remoteAppOpen
                }
            }
        } catch (e: Exception) {
            // Fallback an toàn nếu chưa có google-services.json
        }
    }
}

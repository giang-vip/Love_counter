package com.example.giao_dien.ads

/**
 * File quản lý tập trung toàn bộ Mã Quảng Cáo (Ad Unit ID) trong dự án.
 * Hỗ trợ đồng bộ biến Bật/Tắt và Mã ID linh hoạt từ Firebase Remote Config.
 */
object AdConfig {

    // Cờ Bật/Tắt quảng cáo toàn ứng dụng (Đồng bộ từ Firebase Remote Config)
    var isAdsEnabled: Boolean = true

    // Mã Test Application ID của Google AdMob
    const val ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713"

    // Mã Test Interstitial Ad
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    var remoteInterstitialId: String = INTERSTITIAL_AD_UNIT_ID
        get() = field.ifEmpty { INTERSTITIAL_AD_UNIT_ID }

    // Mã Test Native Ad
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    var remoteNativeId: String = NATIVE_AD_UNIT_ID
        get() = field.ifEmpty { NATIVE_AD_UNIT_ID }

    // Mã Test Banner Ad
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    var remoteBannerId: String = BANNER_AD_UNIT_ID
        get() = field.ifEmpty { BANNER_AD_UNIT_ID }

    // Mã Test Rewarded Ad
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    var remoteRewardedId: String = REWARDED_AD_UNIT_ID
        get() = field.ifEmpty { REWARDED_AD_UNIT_ID }

    // Mã Test App Open Ad
    const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
    var remoteAppOpenId: String = APP_OPEN_AD_UNIT_ID
        get() = field.ifEmpty { APP_OPEN_AD_UNIT_ID }
}

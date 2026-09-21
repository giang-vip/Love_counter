package com.app.love_counter.ads

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.admob.helper.appoppen.AppResumeAdConfig
import com.ads.admob.helper.appoppen.AppResumeAdHelper

/**
 * UTILS QUẢN LÝ QUẢNG CÁO APP OPEN (RESUME ADS)
 * Chức năng: Đang dùng app -> Có người nhắn tin Zalo -> Vuốt ra nhắn tin -> Quay lại App -> BÙM! Hiện quảng cáo mở app.
 */
class AppOpenAdsUtils private constructor() {
    
    // Biến này bắt buộc phải giữ lại (dù IDE báo xám) để tránh bị hệ thống gom rác (Garbage Collector) dọn đi mất.
    private var appResumeAdHelper: AppResumeAdHelper? = null

    companion object {
        @Volatile
        private var instance: AppOpenAdsUtils? = null

        fun getInstance(): AppOpenAdsUtils {
            return instance ?: synchronized(this) {
                instance ?: AppOpenAdsUtils().also { instance = it }
            }
        }
    }

    /**
     * KHỞI TẠO MỘT LẦN DUY NHẤT Ở MYAPPLICATION.KT
     */
    fun initAppOpenResume(application: Application, idAds: String) {
        if (!FirebaseConfigManager.getInstance().adConfig.configAds) return

        val isEnable = FirebaseConfigManager.getInstance().adConfig.appOpenResume

        val config = AppResumeAdConfig(
            idAds = idAds,
            canShowAds = isEnable,
            canReloadAds = true,
            adPlacement = AdPlacement.APP_OPEN_RESUME
        )
        
        // Truyền ProcessLifecycleOwner: Giúp Thư viện biết được toàn bộ App đang ở nền hay đang mở
        appResumeAdHelper = AppResumeAdHelper(
            application,
            ProcessLifecycleOwner.get(),
            config
        )
    }
}

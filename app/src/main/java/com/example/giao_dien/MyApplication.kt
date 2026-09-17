package com.example.giao_dien

import android.app.Application
import com.example.giao_dien.ads.AppOpenAdManager
import com.example.giao_dien.utils.FirebaseRemoteConfigHelper
import com.google.android.gms.ads.MobileAds

class MyApplication : Application() {

    lateinit var appOpenAdManager: AppOpenAdManager
        private set

    override fun onCreate() {
        super.onCreate()
        // Khởi tạo SDK Google Ads khi App bắt đầu chạy
        MobileAds.initialize(this) {}

        // Tải cấu hình Bật/Tắt và ID Quảng Cáo từ xa bằng Firebase Remote Config
        FirebaseRemoteConfigHelper.initAndFetchRemoteConfig()

        // Khởi tạo AppOpenAdManager theo dõi vòng đời App (Resume từ Background)
        appOpenAdManager = AppOpenAdManager(this)
        appOpenAdManager.loadAd()
    }
}

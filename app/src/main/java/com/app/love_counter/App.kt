package com.app.love_counter

import android.app.Application
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.config.AfAdConfig
import com.ads.admob.config.AfAdjustConfig
import com.ads.admob.config.NetworkProvider
import com.ads.admob.listener.AdmobCallBack
import com.app.love_counter.ads.AdConfig
import com.app.love_counter.ads.AppOpenAdsUtils
import com.app.love_counter.ads.FirebaseConfigManager
import com.google.android.gms.ads.MobileAds

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Khởi tạo SDK Google Ads khi App bắt đầu chạy
        MobileAds.initialize(this) {}

        // Tải cấu hình Bật/Tắt và ID Quảng Cáo từ xa bằng Firebase Remote Config và Parse bằng Gson
        FirebaseConfigManager.getInstance().initAndFetchConfig(this)

        // Khởi tạo AdmobFactory (yêu cầu để init FirebaseTrackingManager)
        val adjustConfig = AfAdjustConfig.Build(adjustToken = "dummy_token").build()
        val afAdConfig = AfAdConfig.Builder(afAdjustConfig = adjustConfig)
            .application(this)
            .mediationProvider(NetworkProvider.ADMOB)
            .build()
            
        AdmobFactory.INSTANCE.initAdmob(this, afAdConfig, object : AdmobCallBack {
            override fun initialized() {
                // Đã khởi tạo xong thư viện Admob
            }
        })

        // Khởi tạo AppOpenAdManager theo dõi vòng đời App (Resume từ Background)
        AppOpenAdsUtils.getInstance().initAppOpenResume(this, AdConfig.remoteAppOpenId)
    }
}


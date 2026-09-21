package com.app.love_counter.ads

import android.content.Context
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson

class FirebaseConfigManager private constructor() {

    var adConfig: AdConfig = AdConfig() // Khởi tạo với giá trị default
        private set

    fun initAndFetchConfig(context: Context) {
        try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()

            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0) // Khi release nên đổi thành 3600
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)

            // Parse cache trước
            parseJsonToConfig(remoteConfig.getString("ad_config"))
            
            // Cập nhật giá trị ID nếu có cache
            updateIds(remoteConfig)

            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val jsonConfig = remoteConfig.getString("ad_config")
                    parseJsonToConfig(jsonConfig)
                    updateIds(remoteConfig)
                    Log.d("FirebaseConfigManager", "Fetch remote config success: $jsonConfig")
                } else {
                    Log.e("FirebaseConfigManager", "Fetch remote config failed")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseJsonToConfig(jsonString: String) {
        if (jsonString.isEmpty()) return
        try {
            val config = Gson().fromJson(jsonString, AdConfig::class.java)
            if (config != null) {
                this.adConfig = config
            }
        } catch (e: Exception) {
            Log.e("FirebaseConfigManager", "Lỗi parse json ad_config", e)
        }
    }

    private fun updateIds(remoteConfig: FirebaseRemoteConfig) {
        val interId = remoteConfig.getString("interstitial_ad_id")
        if (interId.isNotEmpty()) AdConfig.remoteInterstitialId = interId

        val nativeId = remoteConfig.getString("native_ad_id")
        if (nativeId.isNotEmpty()) AdConfig.remoteNativeId = nativeId

        val bannerId = remoteConfig.getString("banner_ad_id")
        if (bannerId.isNotEmpty()) AdConfig.remoteBannerId = bannerId

        val appOpenId = remoteConfig.getString("app_open_ad_id")
        if (appOpenId.isNotEmpty()) AdConfig.remoteAppOpenId = appOpenId
    }

    companion object {
        @Volatile
        private var instance: FirebaseConfigManager? = null

        fun getInstance(): FirebaseConfigManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseConfigManager().also { instance = it }
            }
        }
    }
}

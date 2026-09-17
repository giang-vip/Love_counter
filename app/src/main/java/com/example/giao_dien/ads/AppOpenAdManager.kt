package com.example.giao_dien.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.giao_dien.MainActivity
import com.example.giao_dien.MyApplication
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenAdManager(private val myApplication: MyApplication) :
    Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Tải quảng cáo đệm App Open Ad
     */
    fun loadAd() {
        if (isLoadingAd || isAdAvailable()) return

        isLoadingAd = true
        val request = AdRequest.Builder().build()

        AppOpenAd.load(
            myApplication,
            AdConfig.APP_OPEN_AD_UNIT_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = System.currentTimeMillis()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    appOpenAd = null
                    isLoadingAd = false
                }
            }
        )
    }

    /**
     * Kiểm tra ad sẵn sàng và chưa hết hạn (< 4 tiếng)
     */
    fun isAdAvailable(): Boolean {
        return appOpenAd != null && (System.currentTimeMillis() - loadTime < 4 * 3600 * 1000)
    }

    /**
     * Kích hoạt tự động khi người dùng quay lại App từ Background (Sự kiện ON_START)
     */
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAdIfAvailable()
    }

    /**
     * Hiển thị App Open Ad chào mừng khi quay lại App
     */
    fun showAdIfAvailable(activity: Activity? = currentActivity, onAdDismissed: (() -> Unit)? = null) {
        val targetActivity = activity ?: currentActivity ?: run {
            onAdDismissed?.invoke()
            return
        }

        // Bỏ qua không hiện ad ở màn Splash (MainActivity) vì Splash đã có luồng khởi chạy riêng
        if (targetActivity is MainActivity) {
            onAdDismissed?.invoke()
            return
        }

        // Bỏ qua nếu đang có quảng cáo khác hiển thị
        if (isShowingAd || !isAdAvailable()) {
            onAdDismissed?.invoke()
            loadAd()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                onAdDismissed?.invoke()
                loadAd() // Preload ad cho lần sau
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                onAdDismissed?.invoke()
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }

        appOpenAd?.show(targetActivity)
    }

    // ---------- Activity Lifecycle Callbacks ----------

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}

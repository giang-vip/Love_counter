package com.example.giao_dien.ads

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.admob.helper.appoppen.AppResumeAdConfig
import com.ads.admob.helper.appoppen.AppResumeAdHelper

object AppOpenAdsUtils {
    private var appResumeAdHelper: AppResumeAdHelper? = null

    fun initAppOpenResume(application: Application, idAds: String) {
        val config = AppResumeAdConfig(
            idAds = idAds,
            canShowAds = true,
            canReloadAds = true,
            adPlacement = AdPlacement.APP_OPEN_RESUME
        )
        appResumeAdHelper = AppResumeAdHelper(
            application,
            ProcessLifecycleOwner.get(),
            config
        )
    }
}

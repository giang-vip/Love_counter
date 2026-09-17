package com.example.giao_dien.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.giao_dien.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class NativeAdManager {

    private var currentNativeAd: NativeAd? = null

    /**
     * Tải và tự động chèn Native Ad vào Container truyền vào (Có Shimmer Loading)
     */
    fun loadNativeAd(context: Context, container: ViewGroup, layoutResId: Int = R.layout.layout_native_ad_language) {
        // Kiểm tra cờ Bật/Tắt quảng cáo từ xa từ Firebase Remote Config
        if (!AdConfig.isAdsEnabled) {
            container.visibility = View.GONE
            return
        }

        // 1. Hiển thị Shimmer Loading Placeholder trong khi chờ AdMob tải dữ liệu
        val shimmerView = LayoutInflater.from(context).inflate(R.layout.layout_native_ad_shimmer, container, false) as? ShimmerFrameLayout
        if (shimmerView != null) {
            container.removeAllViews()
            container.addView(shimmerView)
            shimmerView.startShimmer()
            container.visibility = View.VISIBLE
        }

        // 2. Gọi AdLoader từ AdMob
        val builder = AdLoader.Builder(context, AdConfig.remoteNativeId)

        builder.forNativeAd { nativeAd ->
            // Hủy NativeAd cũ nếu có
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd

            // Dừng hiệu ứng Shimmer
            shimmerView?.stopShimmer()

            // Inflate layout NativeAdView chính thức
            val adView = LayoutInflater.from(context).inflate(layoutResId, container, false) as NativeAdView

            // Populate các trường dữ liệu vào NativeAdView
            populateNativeAdView(nativeAd, adView, container)

            // Hiển thị quảng cáo vào container
            container.removeAllViews()
            container.addView(adView)
            container.visibility = View.VISIBLE
        }

        val adOptions = NativeAdOptions.Builder().build()
        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Tải ad thất bại ➔ Dừng Shimmer và ẩn container
                shimmerView?.stopShimmer()
                container.visibility = View.GONE
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /**
     * Ánh xạ các View thành phần vào NativeAdView của AdMob
     */
    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView, container: ViewGroup) {
        // 1. Tiêu đề (Headline)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        (adView.headlineView as? TextView)?.text = nativeAd.headline

        // 2. Khung chiếu Hình Ảnh / Video (MediaView)
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        if (nativeAd.mediaContent != null) {
            adView.mediaView?.mediaContent = nativeAd.mediaContent
            adView.mediaView?.visibility = View.VISIBLE
        } else {
            adView.mediaView?.visibility = View.GONE
        }

        // 3. Mô tả (Body)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as? TextView)?.text = nativeAd.body
        }

        // 4. Nút Action (Call To Action)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as? Button)?.text = nativeAd.callToAction
        }

        // 5. Icon App / Brand
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }

        // 6. Xử lý nút bấm [X] góc phải (Nếu được đặt View.VISIBLE)
        val btnClose = adView.findViewById<View>(R.id.btn_close_ad)
        btnClose?.setOnClickListener {
            container.visibility = View.GONE
        }

        // Đăng ký đối tượng NativeAd chính thức cho NativeAdView
        adView.setNativeAd(nativeAd)
    }

    /**
     * Hủy và giải phóng bộ nhớ Native Ad (Gắn vào onDestroy của Activity)
     */
    fun destroyAd() {
        currentNativeAd?.destroy()
        currentNativeAd = null
    }
}

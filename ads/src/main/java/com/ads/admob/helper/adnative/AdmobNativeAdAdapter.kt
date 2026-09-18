package com.ads.admob.helper.adnative

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ads.admob.R
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.data.ContentAd
import com.ads.admob.listener.NativeAdCallback
import com.ads.admob.widget.RecyclerViewAdapterWrapper
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class AdmobNativeAdAdapter(
    private val nativeAdapterConfig: NativeAdapterConfig
) : RecyclerViewAdapterWrapper(nativeAdapterConfig.adapter) {

    private var nativeData: ContentAd? = null

    init {
        setSpanAds()
    }

    private fun getRealItemCount(): Int {
        return nativeAdapterConfig.adapter.itemCount
    }

    private fun getRepeatAdsCount(realCount: Int): Int {
        val firstAdPosition = nativeAdapterConfig.firstPositionNativeApp
        val interval = nativeAdapterConfig.adItemInterval

        if (realCount <= 0) return 0
        if (interval <= 0) return 0
        if (firstAdPosition < 0) return 0
        if (realCount <= firstAdPosition) return 0

        return 1 + ((realCount - firstAdPosition - 1) / interval)
    }

    private fun convertAdPosition2OrgPosition(position: Int): Int {
        val realCount = getRealItemCount()

        if (realCount <= 0) return 0

        val originalPosition = if (nativeAdapterConfig.isRepeat) {
            val firstAdPosition = nativeAdapterConfig.firstPositionNativeApp
            val interval = nativeAdapterConfig.adItemInterval

            if (position < firstAdPosition || interval <= 0) {
                position
            } else {
                val adsBeforeOrAtPosition =
                    1 + ((position - firstAdPosition) / (interval + 1))

                position - adsBeforeOrAtPosition
            }
        } else {
            if (itemCount <= nativeAdapterConfig.firstPositionNativeApp) {
                position
            } else {
                val positionAds =
                    ((nativeAdapterConfig.firstPositionNativeApp + position + 1) /
                            (nativeAdapterConfig.adapter.itemCount + 1).toFloat()).roundToInt()

                if (position > positionAds && positionAds < itemCount) {
                    position - positionAds
                } else {
                    position - ((nativeAdapterConfig.firstPositionNativeApp + position) /
                            (nativeAdapterConfig.adapter.itemCount + 1).toFloat()).roundToInt()
                }
            }
        }

        return min(max(originalPosition, 0), realCount - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) {
            TYPE_FB_NATIVE_ADS
        } else {
            super.getItemViewType(
                convertAdPosition2OrgPosition(position)
            )
        }
    }

    private fun isAdPosition(position: Int): Boolean {
        val realCount = getRealItemCount()
        if (realCount <= 0) return false

        return if (nativeAdapterConfig.isRepeat) {
            val firstAdPosition = nativeAdapterConfig.firstPositionNativeApp
            val interval = nativeAdapterConfig.adItemInterval

            if (interval <= 0) return false
            if (realCount <= firstAdPosition) return false

            if (position < firstAdPosition) {
                false
            } else {
                (position - firstAdPosition) % (interval + 1) == 0
            }
        } else {
            if (itemCount < nativeAdapterConfig.firstPositionNativeApp) {
                false
            } else {
                position == nativeAdapterConfig.firstPositionNativeApp
            }
        }
    }

    override fun getItemCount(): Int {
        val realCount = super.getItemCount()

        if (realCount <= 0) return 0

        return if (nativeAdapterConfig.isRepeat) {
            val adsCount = getRepeatAdsCount(realCount)
            realCount + adsCount
        } else {
            if (realCount <= nativeAdapterConfig.firstPositionNativeApp) {
                realCount
            } else {
                realCount + 1
            }
        }
    }

    private fun onBindAdViewHolder(holder: RecyclerView.ViewHolder) {
        val adHolder = holder as AdViewHolder

        if (nativeAdapterConfig.forceReloadAdOnBind || !adHolder.loaded) {
            AdmobFactory.INSTANCE.requestNativeAd(
                holder.itemView.context,
                nativeAdapterConfig.nativeAdId,
                nativeAdapterConfig.adPlacement,
                nativeAdapterConfig.reloadIfFirstFail,
                object : NativeAdCallback {
                    override fun populateNativeAd() {}

                    override fun onAdLoaded(data: ContentAd) {
                        nativeData = data

                        AdmobFactory.INSTANCE.populateNativeAdView(
                            holder.itemView.context,
                            data,
                            nativeAdapterConfig.nativeContentView,
                            holder.nativeContentView,
                            holder.shimmerLayoutView,
                            adPlacement = nativeAdapterConfig.adPlacement,
                            object : NativeAdCallback {
                                override fun populateNativeAd() {}

                                override fun onAdLoaded(data: ContentAd) {
                                    adHolder.loaded = true
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    adHolder.loaded = false
                                }

                                override fun onAdClicked() {}

                                override fun onAdImpression() {}

                                override fun onAdFailedToShow(adError: AdError) {
                                    adHolder.loaded = false
                                }
                            }
                        )
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        adHolder.loaded = false
                    }

                    override fun onAdClicked() {}

                    override fun onAdImpression() {}

                    override fun onAdFailedToShow(adError: AdError) {
                        adHolder.loaded = false
                    }
                }
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindAdViewHolder(holder)
        } else {
            val originalPosition = convertAdPosition2OrgPosition(position)
            super.onBindViewHolder(holder, originalPosition)
        }
    }

    private fun onCreateAdViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val adLayoutOutline = inflater.inflate(
            nativeAdapterConfig.itemNativeAd,
            parent,
            false
        )

        return AdViewHolder(adLayoutOutline)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FB_NATIVE_ADS) {
            onCreateAdViewHolder(parent)
        } else {
            super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder.itemViewType != TYPE_FB_NATIVE_ADS) {
            super.onViewRecycled(holder)
        }
    }

    private fun setSpanAds() {
        val gridLayoutManager =
            nativeAdapterConfig.gridLayoutManager as? GridLayoutManager ?: return

        val oldSpanSizeLookup = gridLayoutManager.spanSizeLookup

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isAdPosition(position)) {
                    gridLayoutManager.spanCount
                } else {
                    oldSpanSizeLookup.getSpanSize(
                        convertAdPosition2OrgPosition(position)
                    )
                }
            }
        }
    }

    private class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var loaded = false

        var shimmerLayoutView: ShimmerFrameLayout =
            view.findViewById(R.id.shimmer_container_native)

        var nativeContentView: FrameLayout =
            view.findViewById(R.id.frAds)
    }

    companion object {
        const val TYPE_FB_NATIVE_ADS = 900
    }
}
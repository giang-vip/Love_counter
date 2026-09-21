package com.app.love_counter.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.app.love_counter.R
import com.app.love_counter.ads.AdConfig
import com.app.love_counter.ads.AdPlacement
import com.app.love_counter.ads.FirebaseConfigManager
import com.app.love_counter.ads.NativeAdsUtils
import com.app.love_counter.data.local.AppPreferences
import com.app.love_counter.data.repository.AppRepositoryImpl
import com.app.love_counter.databinding.ActivityIntroBinding
import com.app.love_counter.ui.permission.PermissionActivity
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    private val viewModel: IntroViewModel by viewModels {
        val prefs = AppPreferences(applicationContext)
        val repository = AppRepositoryImpl(prefs)
        IntroViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAds()
        setupViewPager()
        bindActions()
        observeViewModel()
    }

    /**
     * KHỞI TẠO QUẢNG CÁO: Hiển thị ad hiện tại và preload ad tiếp theo
     */
    private fun initAds() {
        // Hiển thị Native Ad Intro đã được tải mồi từ LanguageActivity
        NativeAdsUtils.getInstance().showNativeAds(
            container = binding.nativeAdContainer,
            adPlacement = AdPlacement.NATIVE_INTRO
        )

        // Tải mồi (Preload) quảng cáo Native cho màn hình Permission tiếp theo
        NativeAdsUtils.getInstance().loadNativeAds(
            activity = this,
            idAds = AdConfig.remoteNativeId,
            adPlacement = AdPlacement.NATIVE_PERMISSION,
            isEnable = FirebaseConfigManager.getInstance().adConfig.nativePermission
        )
    }

    /**
     * CẤU HÌNH CÁC HỢP PHẦN GIAO DIỆN (VIEW PAGER)
     */
    private fun setupViewPager() {
        binding.viewPagerIntro.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.onPageChanged(position)
                }
            }
        )
    }

    /**
     * THIẾT LẬP CÁC SỰ KIỆN CLICK HÀNH ĐỘNG CỦA VIEW
     */
    private fun bindActions() {
        binding.tvNext.setOnClickListener {
            viewModel.onNextClicked()
        }
    }

    /**
     * OBSERVE STATEFLOW VÀ CẬP NHẬT TRẠNG THÁI GIAO DIỆN
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        if (state.pages.isNotEmpty() && binding.viewPagerIntro.adapter == null) {
                            binding.viewPagerIntro.adapter = IntroPagerAdapter(state.pages)
                        }

                        if (binding.viewPagerIntro.currentItem != state.currentPageIndex) {
                            binding.viewPagerIntro.currentItem = state.currentPageIndex
                        }

                        if (state.pages.isNotEmpty()) {
                            val currentPage = state.pages[state.currentPageIndex]
                            binding.tvIntroTitle.setText(currentPage.titleRes)
                            binding.tvIntroDescription.setText(currentPage.descriptionRes)
                        }

                        updateDots(state.currentPageIndex)
                    }
                }
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is IntroEvent.NavigateToPermission -> {
                                val intent = Intent(this@IntroActivity, PermissionActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateDots(position: Int) {
        when (position) {
            0 -> {
                binding.dot1.layoutParams.width = dpToPx(14)
                binding.dot2.layoutParams.width = dpToPx(5)
                binding.dot3.layoutParams.width = dpToPx(5)
                binding.dot1.setBackgroundResource(R.drawable.bg_dot_active)
                binding.dot2.setBackgroundResource(R.drawable.bg_dot)
                binding.dot3.setBackgroundResource(R.drawable.bg_dot)
            }
            1 -> {
                binding.dot1.layoutParams.width = dpToPx(5)
                binding.dot2.layoutParams.width = dpToPx(14)
                binding.dot3.layoutParams.width = dpToPx(5)
                binding.dot1.setBackgroundResource(R.drawable.bg_dot)
                binding.dot2.setBackgroundResource(R.drawable.bg_dot_active)
                binding.dot3.setBackgroundResource(R.drawable.bg_dot)
            }
            else -> {
                binding.dot1.layoutParams.width = dpToPx(5)
                binding.dot2.layoutParams.width = dpToPx(5)
                binding.dot3.layoutParams.width = dpToPx(14)
                binding.dot1.setBackgroundResource(R.drawable.bg_dot)
                binding.dot2.setBackgroundResource(R.drawable.bg_dot)
                binding.dot3.setBackgroundResource(R.drawable.bg_dot_active)
            }
        }

        binding.dot1.requestLayout()
        binding.dot2.requestLayout()
        binding.dot3.requestLayout()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

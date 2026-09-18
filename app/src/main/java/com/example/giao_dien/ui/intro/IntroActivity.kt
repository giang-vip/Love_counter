package com.example.giao_dien.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.giao_dien.R
import com.example.giao_dien.ads.AdConfig
import com.example.giao_dien.ads.AdPlacement
import com.example.giao_dien.ads.FirebaseConfigManager
import com.example.giao_dien.ads.NativeAdsUtils
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.repository.AppRepositoryImpl
import com.example.giao_dien.databinding.ActivityIntroBinding
import com.example.giao_dien.ui.permission.PermissionActivity
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

        setupViewPager()
        setupNextButton()
        observeViewModel()

        // Load Native Ad
        NativeAdsUtils.loadAndShowNativeAds(
            activity = this,
            lifecycleOwner = this,
            idAds = AdConfig.remoteNativeId,
            adPlacement = AdPlacement.NATIVE_INTRO,
            isEnable = FirebaseConfigManager.getInstance().adConfig.nativeIntro,
            container = binding.nativeAdContainer
        )
    }

    private fun setupNextButton() {
        binding.tvNext.setOnClickListener {
            viewModel.onNextClicked()
        }
    }

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

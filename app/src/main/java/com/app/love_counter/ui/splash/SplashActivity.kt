package com.app.love_counter.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.love_counter.R
import com.app.love_counter.ads.AdConfig
import com.app.love_counter.ads.AdPlacement
import com.app.love_counter.ads.FirebaseConfigManager
import com.app.love_counter.ads.InterAdsUtils
import com.app.love_counter.ads.NativeAdsUtils
import com.app.love_counter.data.local.AppPreferences
import com.app.love_counter.data.repository.AppRepositoryImpl
import com.app.love_counter.databinding.ActivitySplashBinding
import com.app.love_counter.ui.home.HomeActivity
import com.app.love_counter.ui.intro.IntroActivity
import com.app.love_counter.ui.language.LanguageActivity
import com.app.love_counter.ui.permission.PermissionActivity
import com.app.love_counter.utils.NetworkUtils
import kotlinx.coroutines.launch

/**
 * MainActivity đóng vai trò Màn hình Mở đầu (Splash Screen).
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by viewModels {
        val prefs = AppPreferences(applicationContext)
        val repository = AppRepositoryImpl(prefs)
        SplashViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAds()
        bindActions()
        observeViewModel()
    }

    /**
     * KHỞI TẠO QUẢNG CÁO: Đặt ở đầu theo quy ước code của công ty.
     * Thực hiện tải mồi ngầm các quảng cáo cần thiết cho màn hình tiếp theo.
     */
    private fun initAds() {
        NativeAdsUtils.getInstance().loadNativeAds(
            activity = this,
            idAds = AdConfig.remoteNativeId,
            adPlacement = AdPlacement.NATIVE_LANGUAGE,
            isEnable = FirebaseConfigManager.getInstance().adConfig.nativeLanguage
        )
    }

    /**
     * THIẾT LẬP CÁC SỰ KIỆN CLICK / HÀNH ĐỘNG CỦA VIEW
     */
    private fun bindActions() {
        viewModel.startLoadingAnimation()
    }

    private fun showAdThenNavigate(targetClass: Class<*>) {
        // [KIỂM TRA INTERNET TRƯỚC KHI GỌI SPLASH]
        // Base yêu cầu: Nếu không có mạng, không cho vào App.
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNoInternetDialog(targetClass)
            return
        }

        InterAdsUtils.getInstance().showSplashAds(
            activity = this,
            lifecycleOwner = this,
            idAds = AdConfig.remoteInterstitialId,
            adPlacement = AdPlacement.INTER_SPLASH,
            isEnable = FirebaseConfigManager.getInstance().adConfig.interSplash,
            action = { navigateToScreen(targetClass) }
        )
    }

    private fun showNoInternetDialog(targetClass: Class<*>) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.no_internet_title))
            .setMessage(getString(R.string.no_internet_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.retry)) { dialog, _ ->
                dialog.dismiss()
                viewModel.startLoadingAnimation() // Chạy lại logic
            }
            .setNegativeButton(getString(R.string.exit)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SplashState.Loading -> {
                            updateDots(state.activeDotIndex)
                        }
                        is SplashState.NavigateToLanguage -> {
                            showAdThenNavigate(LanguageActivity::class.java)
                        }
                        is SplashState.NavigateToIntro -> {
                            showAdThenNavigate(IntroActivity::class.java)
                        }
                        is SplashState.NavigateToPermission -> {
                            showAdThenNavigate(PermissionActivity::class.java)
                        }
                        is SplashState.NavigateToHome -> {
                            showAdThenNavigate(HomeActivity::class.java)
                        }
                    }
                }
            }
        }
    }

    private fun updateDots(activeIndex: Int) {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4)
        dots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(if (index == activeIndex) R.drawable.bg_dot_active else R.drawable.bg_dot)
        }
    }

    private fun navigateToScreen(targetClass: Class<*>) {
        val intent = Intent(this, targetClass)
        startActivity(intent)
        finish()
    }
}
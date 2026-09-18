package com.example.giao_dien

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giao_dien.ads.AdConfig
import com.example.giao_dien.ads.AdPlacement
import com.example.giao_dien.ads.FirebaseConfigManager
import com.example.giao_dien.ads.InterAdsUtils
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.repository.AppRepositoryImpl
import com.example.giao_dien.databinding.ActivityMainBinding
import com.example.giao_dien.ui.home.HomeActivity
import com.example.giao_dien.ui.intro.IntroActivity
import com.example.giao_dien.ui.language.LanguageActivity
import com.example.giao_dien.ui.permission.PermissionActivity
import com.example.giao_dien.ui.splash.SplashState
import com.example.giao_dien.ui.splash.SplashViewModel
import kotlinx.coroutines.launch

/**
 * MainActivity đóng vai trò Màn hình Mở đầu (Splash Screen).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: SplashViewModel by viewModels {
        val prefs = AppPreferences(applicationContext)
        val repository = AppRepositoryImpl(prefs)
        SplashViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        viewModel.startLoadingAnimation()
    }

    private fun showAdThenNavigate(targetClass: Class<*>) {
        InterAdsUtils.showSplashAds(
            activity = this,
            lifecycleOwner = this,
            idAds = AdConfig.remoteInterstitialId,
            adPlacement = AdPlacement.INTER_SPLASH,
            isEnable = FirebaseConfigManager.getInstance().adConfig.interSplash,
            action = { navigateToScreen(targetClass) }
        )
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

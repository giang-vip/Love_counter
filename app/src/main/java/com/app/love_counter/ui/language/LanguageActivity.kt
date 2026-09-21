package com.app.love_counter.ui.language

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.love_counter.ads.AdConfig
import com.app.love_counter.ads.AdPlacement
import com.app.love_counter.ads.FirebaseConfigManager
import com.app.love_counter.ads.NativeAdsUtils
import com.app.love_counter.data.local.AppPreferences
import com.app.love_counter.data.repository.AppRepositoryImpl
import com.app.love_counter.databinding.ActivityLanguageBinding
import com.app.love_counter.ui.intro.IntroActivity
import kotlinx.coroutines.launch

class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private var isFromSetting: Boolean = false

    private val viewModel: LanguageViewModel by viewModels {
        val prefs = AppPreferences(applicationContext)
        val repository = AppRepositoryImpl(prefs)
        LanguageViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initAds()
        setupRecyclerView()
        bindActions()
        observeViewModel()
    }

    /**
     * KHỞI TẠO CẤU HÌNH BAN ĐẦU CỦA VIEWS / TOOLBAR
     */
    private fun initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isFromSetting = intent.getBooleanExtra(EXTRA_IS_FROM_SETTING, false)
        if (isFromSetting) {
            binding.btnBack.visibility = View.VISIBLE
        } else {
            binding.btnBack.visibility = View.GONE
        }
    }

    /**
     * KHỞI TẠO QUẢNG CÁO: Hiển thị ad hiện tại và tải mồi cho ad tiếp theo
     */
    private fun initAds() {
        // Hiển thị Native Ad Language đã được tải mồi từ MainActivity
        NativeAdsUtils.getInstance().showNativeAds(
            container = binding.nativeAdContainer,
            adPlacement = AdPlacement.NATIVE_LANGUAGE
        )

        // Tải mồi (Preload) quảng cáo Native cho màn hình Intro tiếp theo
        NativeAdsUtils.getInstance().loadNativeAds(
            activity = this,
            idAds = AdConfig.remoteNativeId,
            adPlacement = AdPlacement.NATIVE_INTRO,
            isEnable = FirebaseConfigManager.getInstance().adConfig.nativeIntro
        )
    }

    /**
     * CẤU HÌNH RECYCLERVIEW / ADAPTER
     */
    private fun setupRecyclerView() {
        adapter = LanguageAdapter { language ->
            viewModel.selectLanguage(language.code)
        }
        binding.rvLanguage.layoutManager = LinearLayoutManager(this)
        binding.rvLanguage.adapter = adapter
    }

    /**
     * THIẾT LẬP CÁC SỰ KIỆN CLICK HÀNH ĐỘNG
     */
    private fun bindActions() {
        binding.btnBack.setOnClickListener { finish() }

        binding.ivTitleCheck.setOnClickListener {
            viewModel.confirmLanguage()
        }
    }

    /**
     * OBSERVE STATEFLOW VÀ CẬP NHẬT GIAO DIỆN
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        adapter.submitList(state.languages)
                    }
                }
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is LanguageEvent.NavigateToIntro -> {
                                changeLanguageAndNavigate(event.languageCode)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeLanguageAndNavigate(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
        if (isFromSetting) {
            finish()
        } else {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_IS_FROM_SETTING = "extra_is_from_setting"
    }
}

package com.example.giao_dien.ui.language

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
import com.example.giao_dien.ads.AdConfig
import com.example.giao_dien.ads.AdPlacement
import com.example.giao_dien.ads.FirebaseConfigManager
import com.example.giao_dien.ads.NativeAdsUtils
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.repository.AppRepositoryImpl
import com.example.giao_dien.databinding.ActivityLanguageBinding
import com.example.giao_dien.ui.intro.IntroActivity
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

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        // Load Native Ad
        NativeAdsUtils.loadAndShowNativeAds(
            activity = this,
            lifecycleOwner = this,
            idAds = AdConfig.remoteNativeId,
            adPlacement = AdPlacement.NATIVE_LANGUAGE,
            isEnable = FirebaseConfigManager.getInstance().adConfig.nativeLanguage,
            container = binding.nativeAdContainer
        )
    }

    private fun setupRecyclerView() {
        adapter = LanguageAdapter { language ->
            viewModel.selectLanguage(language.code)
        }
        binding.rvLanguage.layoutManager = LinearLayoutManager(this)
        binding.rvLanguage.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.ivTitleCheck.setOnClickListener {
            viewModel.confirmLanguage()
        }
    }

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

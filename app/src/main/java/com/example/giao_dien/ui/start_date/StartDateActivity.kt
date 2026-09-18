package com.example.giao_dien.ui.start_date

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giao_dien.ads.AdConfig
import com.example.giao_dien.ads.AdPlacement
import com.example.giao_dien.ads.FirebaseConfigManager
import com.example.giao_dien.ads.InterAdsUtils
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.repository.CoupleRepositoryImpl
import com.example.giao_dien.databinding.ActivityStartDateBinding
import com.example.giao_dien.utils.DatePickerDialogHelper
import kotlinx.coroutines.launch

class StartDateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartDateBinding

    private val viewModel: StartDateViewModel by viewModels {
        val repository = CoupleRepositoryImpl(applicationContext)
        StartDateViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStartDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.layoutDateOfLove.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnDone.setOnClickListener {
            viewModel.save()
        }
    }

    private fun showDatePickerDialog() {
        DatePickerDialogHelper.showCustomDatePickerDialog(
            this,
            binding.tvDateValue.text.toString()
        ) { dateStr ->
            viewModel.updateStartDate(dateStr)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.startDateState.collect { date ->
                        binding.tvDateValue.text = date
                    }
                }
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is StartDateEvent.SavedSuccessfully -> {
                                InterAdsUtils.showSplashAds(
                                    activity = this@StartDateActivity,
                                    lifecycleOwner = this@StartDateActivity,
                                    idAds = AdConfig.remoteInterstitialId,
                                    adPlacement = AdPlacement.INTER_SPLASH, // Hoặc định nghĩa cờ riêng
                                    isEnable = FirebaseConfigManager.getInstance().adConfig.interSplash,
                                    action = { finish() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
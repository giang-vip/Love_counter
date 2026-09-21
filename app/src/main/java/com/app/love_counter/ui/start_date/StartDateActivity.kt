package com.app.love_counter.ui.start_date

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.love_counter.ads.AdConfig
import com.app.love_counter.ads.AdPlacement
import com.app.love_counter.ads.FirebaseConfigManager
import com.app.love_counter.ads.InterAdsUtils
import com.app.love_counter.data.repository.CoupleRepositoryImpl
import com.app.love_counter.databinding.ActivityStartDateBinding
import com.app.love_counter.utils.DatePickerDialogHelper
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

        initViews()
        initAds()
        bindActions()
        observeViewModel()
    }

    /**
     * KHỞI TẠO CẤU HÌNH BAN ĐẦU CỦA VIEWS / INSETS
     */
    private fun initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * KHỞI TẠO QUẢNG CÁO: Tải mồi (Preload) Quảng Cáo
     */
    private fun initAds() {
        InterAdsUtils.getInstance().loadInterAds(
            activity = this,
            idAds = AdConfig.remoteInterstitialId,
            adPlacement = AdPlacement.INTER_FEATURE,
            isEnable = FirebaseConfigManager.getInstance().adConfig.interFeature,
            isLoadAndShow = false
        )
    }

    /**
     * THIẾT LẬP CÁC SỰ KIỆN CLICK HÀNH ĐỘNG CỦA VIEW
     */
    private fun bindActions() {
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

    /**
     * OBSERVE STATEFLOW VÀ CẬP NHẬT TRẠNG THÁI GIAO DIỆN
     */
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
                                InterAdsUtils.getInstance().showInterAdsV2(
                                    activity = this@StartDateActivity,
                                    lifecycleOwner = this@StartDateActivity,
                                    adPlacement = AdPlacement.INTER_FEATURE,
                                    isEnable = FirebaseConfigManager.getInstance().adConfig.interFeature,
                                    isReloadWhenClose = true,
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

package com.app.love_counter.ui.horoscope

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
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
import com.app.love_counter.ads.RewardAdsUtils
import com.app.love_counter.data.repository.HoroscopeRepositoryImpl
import com.app.love_counter.databinding.ActivityHoroscopeTestBinding
import kotlinx.coroutines.launch

class HoroscopeTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHoroscopeTestBinding

    private val viewModel: HoroscopeTestViewModel by viewModels {
        val repository = HoroscopeRepositoryImpl(applicationContext)
        HoroscopeTestViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHoroscopeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAds()
        bindActions()
        observeViewModel()
    }

    /**
     * KHỞI TẠO QUẢNG CÁO: Preload Rewarded Video Ad khi mở màn hình
     */
    private fun initAds() {
        RewardAdsUtils.getInstance().loadRewardAds(
            activity = this,
            idAds = AdConfig.remoteRewardedId,
            adPlacement = AdPlacement.REWARD_HOROSCOPE,
            isEnable = FirebaseConfigManager.getInstance().adConfig.rewardHoroscope
        )
    }

    /**
     * THIẾT LẬP CÁC SỰ KIỆN CLICK HÀNH ĐỘNG CỦA VIEW
     */
    private fun bindActions() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSelectYourHoroscope.setOnClickListener {
            showBottomSheet(isYour = true)
        }

        binding.btnSelectPartnerHoroscope.setOnClickListener {
            showBottomSheet(isYour = false)
        }

        binding.btnStartTest.setOnClickListener {
            viewModel.startLoveTest()
        }
    }

    private fun showBottomSheet(isYour: Boolean) {
        val sheet = HoroscopeBottomSheetDialogFragment(viewModel.allHoroscopes) { selectedItem ->
            if (isYour) {
                viewModel.selectYourHoroscope(selectedItem)
            } else {
                viewModel.selectPartnerHoroscope(selectedItem)
            }
        }
        sheet.show(supportFragmentManager, HoroscopeBottomSheetDialogFragment.TAG)
    }

    /**
     * OBSERVE STATEFLOW VÀ RENDERING UI TRẠNG THÁI
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.yourHoroscope.collect { item ->
                        binding.tvYourHoroscopeName.text = item.name
                        binding.imgYourHoroscope.setImageResource(item.iconRes)
                    }
                }

                launch {
                    viewModel.partnerHoroscope.collect { item ->
                        binding.tvPartnerHoroscopeName.text = item.name
                        binding.imgPartnerHoroscope.setImageResource(item.iconRes)
                    }
                }

                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is HoroscopeTestEvent.NavigateToResult -> {
                                val intent = Intent(this@HoroscopeTestActivity, HoroscopeTestResultActivity::class.java).apply {
                                    putExtra(EXTRA_H1_ID, event.h1Id)
                                    putExtra(EXTRA_H2_ID, event.h2Id)
                                }

                                // Yêu cầu bắt buộc xem Rewarded Video Ad mới cho chuyển sang màn kết quả
                                promptRewardedAdToViewResult {
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Hiển thị Dialog yêu cầu người dùng xem Video Rewarded Ad để mở khóa kết quả
     */
    private fun promptRewardedAdToViewResult(onSuccess: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rewarded_ad_prompt, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnWatch = dialogView.findViewById<Button>(R.id.btnWatchVideoPrompt)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelPrompt)

        btnWatch?.setOnClickListener {
            dialog.dismiss()
            RewardAdsUtils.getInstance().showRewardAds(
                activity = this,
                lifecycleOwner = this,
                adPlacement = AdPlacement.REWARD_HOROSCOPE,
                isEnable = FirebaseConfigManager.getInstance().adConfig.rewardHoroscope,
                onEarned = {
                    onSuccess()
                },
                onNextAction = {
                    Toast.makeText(this, "Bạn cần xem hết video để xem báo cáo kết quả tương hợp!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        btnCancel?.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Bạn cần xem hết video để xem báo cáo kết quả tương hợp!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    companion object {
        const val EXTRA_H1_ID = "extra_h1_id"
        const val EXTRA_H2_ID = "extra_h2_id"
    }
}

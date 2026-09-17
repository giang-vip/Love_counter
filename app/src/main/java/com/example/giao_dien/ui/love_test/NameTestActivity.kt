package com.example.giao_dien.ui.love_test

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
import com.example.giao_dien.R
import com.example.giao_dien.ads.RewardedAdManager
import com.example.giao_dien.databinding.ActivityNameTestBinding
import kotlinx.coroutines.launch

class NameTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNameTestBinding

    private val viewModel: NameTestViewModel by viewModels {
        NameTestViewModel.Factory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNameTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Preload Rewarded Video Ad khi mở màn hình
        RewardedAdManager.getInstance().loadAd(this)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnStartTest.setOnClickListener {
            viewModel.startLoveTest(
                binding.edtYourName.text.toString(),
                binding.edtPartnerName.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is NameTestEvent.NavigateToResult -> {
                            val intent = Intent(this@NameTestActivity, NameTestResultActivity::class.java).apply {
                                putExtra(EXTRA_YOUR_NAME, event.yourName)
                                putExtra(EXTRA_PARTNER_NAME, event.partnerName)
                            }

                            // Yêu cầu bắt buộc xem Rewarded Video Ad mới cho chuyển sang màn kết quả
                            promptRewardedAdToViewResult {
                                startActivity(intent)
                            }
                        }
                        is NameTestEvent.ShowToast -> {
                            Toast.makeText(this@NameTestActivity, getString(event.messageRes), Toast.LENGTH_SHORT).show()
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
            if (RewardedAdManager.getInstance().isAdAvailable()) {
                RewardedAdManager.getInstance().showAd(
                    activity = this,
                    onRewardEarned = {
                        // Người dùng đã xem HẾT 30s video ➔ Cho phép xem kết quả!
                        onSuccess()
                    },
                    onAdDismissed = {
                        // Bấm tắt giữa chừng ➔ Thông báo yêu cầu xem hết video
                        Toast.makeText(this, "Bạn cần xem hết video để xem báo cáo kết quả tương hợp!", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // Nếu Ad chưa sẵn sàng (mạng chậm), cho phép xem kết quả không bắt chờ
                onSuccess()
            }
        }

        btnCancel?.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Bạn cần xem hết video để xem báo cáo kết quả tương hợp!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    companion object {
        const val EXTRA_YOUR_NAME = "extra_your_name"
        const val EXTRA_PARTNER_NAME = "extra_partner_name"
    }
}

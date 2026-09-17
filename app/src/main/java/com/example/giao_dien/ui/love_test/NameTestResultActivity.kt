package com.example.giao_dien.ui.love_test

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giao_dien.R
import com.example.giao_dien.data.repository.NameTestRepositoryImpl
import com.example.giao_dien.databinding.ActivityNameTestResultBinding
import kotlinx.coroutines.launch

class NameTestResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNameTestResultBinding
    private var hasAnimated = false

    private val viewModel: NameTestResultViewModel by viewModels {
        val yourName = intent.getStringExtra(NameTestActivity.EXTRA_YOUR_NAME) ?: ""
        val partnerName = intent.getStringExtra(NameTestActivity.EXTRA_PARTNER_NAME) ?: ""
        val repository = NameTestRepositoryImpl()
        NameTestResultViewModel.Factory(yourName, partnerName, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNameTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnRetry.setOnClickListener { finish() }

        binding.btnShare.setOnClickListener {
            val result = viewModel.resultState.value
            val shareText = getString(
                R.string.share_name_test_format,
                result.yourName,
                result.partnerName,
                result.percent,
                result.description
            )
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_chooser_title))
            startActivity(shareIntent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultState.collect { result ->
                    binding.tvName1.text = result.yourName
                    binding.tvName2.text = result.partnerName
                    binding.tvResultDescription.text = result.description

                    if (!hasAnimated) {
                        hasAnimated = true
                        startAnalyzingAnimation(result.percent)
                    } else {
                        binding.tvMatchPercent.text = "${result.percent}%"
                    }
                }
            }
        }
    }

    /**
     * Chạy Animation đếm % tự nhiên từ 0% ➔ % kết quả thực tế trong 2.5s
     */
    private fun startAnalyzingAnimation(targetPercent: Int) {
        val animator = ValueAnimator.ofInt(0, targetPercent)
        animator.duration = 2500
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            binding.tvMatchPercent.text = "$value%"
        }
        animator.start()
    }
}

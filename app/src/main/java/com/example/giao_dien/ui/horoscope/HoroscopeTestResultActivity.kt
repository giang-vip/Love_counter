package com.example.giao_dien.ui.horoscope

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
import com.example.giao_dien.data.repository.HoroscopeRepositoryImpl
import com.example.giao_dien.databinding.ActivityHoroscopeTestResultBinding
import kotlinx.coroutines.launch

class HoroscopeTestResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHoroscopeTestResultBinding
    private var hasAnimated = false

    private val viewModel: HoroscopeTestResultViewModel by viewModels {
        val h1Id = intent.getStringExtra(HoroscopeTestActivity.EXTRA_H1_ID) ?: "aries"
        val h2Id = intent.getStringExtra(HoroscopeTestActivity.EXTRA_H2_ID) ?: "aries"
        val repository = HoroscopeRepositoryImpl()
        HoroscopeTestResultViewModel.Factory(h1Id, h2Id, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHoroscopeTestResultBinding.inflate(layoutInflater)
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
                R.string.share_horoscope_test_format,
                result.horoscope1.name,
                result.horoscope2.name,
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
                    binding.tvResultHoroscopeName1.text = result.horoscope1.name
                    binding.imgResultHoroscope1.setImageResource(result.horoscope1.iconRes)

                    binding.tvResultHoroscopeName2.text = result.horoscope2.name
                    binding.imgResultHoroscope2.setImageResource(result.horoscope2.iconRes)

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

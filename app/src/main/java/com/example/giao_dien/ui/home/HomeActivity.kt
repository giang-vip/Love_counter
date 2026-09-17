package com.example.giao_dien.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.giao_dien.ads.BannerAdManager
import com.example.giao_dien.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val bannerAdManager = BannerAdManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupBottomNav()

        // Tải Adaptive Banner Ad cố định ở đáy màn hình
        bannerAdManager.loadBannerAd(this, binding.bannerAdContainer)
    }

    private fun setupViewPager() {
        val adapter = HomePagerAdapter(this)
        binding.viewPagerHome.adapter = adapter
        binding.viewPagerHome.isUserInputEnabled = false

        binding.viewPagerHome.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateBottomNavUI(position)
                }
            }
        )
    }

    private fun setupBottomNav() {
        binding.btnNavHome.setOnClickListener {
            binding.viewPagerHome.currentItem = 0
        }
        binding.btnNavLoveTest.setOnClickListener {
            binding.viewPagerHome.currentItem = 1
        }
        binding.btnNavMemory.setOnClickListener {
            binding.viewPagerHome.currentItem = 2
        }
    }

    private fun updateBottomNavUI(position: Int) {
        val activeColor = Color.parseColor("#FF4081")
        val inactiveColor = Color.parseColor("#666666")

        binding.ivNavHome.setColorFilter(if (position == 0) activeColor else inactiveColor)
        binding.tvNavHome.setTextColor(if (position == 0) activeColor else inactiveColor)

        binding.ivNavLoveTest.setColorFilter(if (position == 1) activeColor else inactiveColor)
        binding.tvNavLoveTest.setTextColor(if (position == 1) activeColor else inactiveColor)

        binding.ivNavMemory.setColorFilter(if (position == 2) activeColor else inactiveColor)
        binding.tvNavMemory.setTextColor(if (position == 2) activeColor else inactiveColor)
    }

    override fun onDestroy() {
        bannerAdManager.destroyAd()
        super.onDestroy()
    }
}
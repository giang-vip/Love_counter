package com.app.love_counter.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.app.love_counter.ads.AdConfig
import com.app.love_counter.ads.AdPlacement
import com.app.love_counter.ads.BannerAdsUtils
import com.app.love_counter.ads.FirebaseConfigManager
import com.app.love_counter.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAds()
        initViews()
        bindActions()
    }

    /**
     * KHỞI TẠO QUẢNG CÁO: Đưa lên đầu theo quy ước code công ty
     */
    private fun initAds() {
        BannerAdsUtils.getInstance().initBannerHome(
            activity = this,
            lifecycleOwnerActivity = this,
            idAds = AdConfig.remoteBannerId,
            adPlacement = AdPlacement.BANNER_HOME,
            isEnable = FirebaseConfigManager.getInstance().adConfig.bannerHome,
            container = binding.bannerAdContainer
        )
    }

    /**
     * KHỞI TẠO VIEWS / VIEW PAGER
     */
    private fun initViews() {
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

    /**
     * THIẾT LẬP CÁC SỰ KIỆN CLICK HÀNH ĐỘNG CỦA VIEW
     */
    private fun bindActions() {
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

    /**
     * CẬP NHẬT TRẠNG THÁI MÀU SẮC BOTTOM NAVIGATION
     */
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
}

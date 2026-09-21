package com.app.love_counter.ui.background

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.app.love_counter.ads.AdConfig
import com.app.love_counter.ads.AdPlacement
import com.app.love_counter.ads.FirebaseConfigManager
import com.app.love_counter.ads.InterAdsUtils
import com.app.love_counter.data.repository.CoupleRepositoryImpl
import com.app.love_counter.databinding.ActivityChooseBackgroundBinding
import com.app.love_counter.utils.ImageStorageManager
import kotlinx.coroutines.launch

class ChooseBackgroundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseBackgroundBinding

    private val viewModel: ChooseBackgroundViewModel by viewModels {
        val repository = CoupleRepositoryImpl(applicationContext)
        ChooseBackgroundViewModel.Factory(repository)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = "background_${System.currentTimeMillis()}.png"
            val localUri = ImageStorageManager.saveImageToInternalStorage(this, it, fileName)
            if (localUri != null) {
                viewModel.selectBackgroundUri(localUri.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBackgroundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAds()
        setupRecyclerView()
        bindActions()
        observeViewModel()
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
     * CẤU HÌNH RECYCLERVIEW
     */
    private fun setupRecyclerView() {
        binding.rvBackgrounds.layoutManager = GridLayoutManager(this, 2)
    }

    /**
     * THIẾT LẬP CÁC SỰ KIỆN CLICK HÀNH ĐỘNG CỦA VIEW
     */
    private fun bindActions() {
        binding.viewDismiss.setOnClickListener { finish() }
    }

    /**
     * OBSERVE STATEFLOW VÀ CẬP NHẬT TRẠNG THÁI GIAO DIỆN
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.backgroundItems.collect { items ->
                        binding.rvBackgrounds.adapter = BackgroundAdapter(items) { item ->
                            if (item.isAddButton) {
                                galleryLauncher.launch("image/*")
                            } else if (item.drawableRes != null) {
                                viewModel.selectBackgroundUri("drawable://${item.drawableRes}")
                            } else if (item.uriString != null) {
                                viewModel.selectBackgroundUri(item.uriString)
                            }
                        }
                    }
                }
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is BackgroundEvent.BackgroundSaved -> {
                                InterAdsUtils.getInstance().showInterAdsV2(
                                    activity = this@ChooseBackgroundActivity,
                                    lifecycleOwner = this@ChooseBackgroundActivity,
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

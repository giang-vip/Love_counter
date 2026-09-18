package com.example.giao_dien.ui.background

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giao_dien.ads.AdConfig
import com.example.giao_dien.ads.AdPlacement
import com.example.giao_dien.ads.FirebaseConfigManager
import com.example.giao_dien.ads.InterAdsUtils
import com.example.giao_dien.data.repository.CoupleRepositoryImpl
import com.example.giao_dien.databinding.ActivityChooseBackgroundBinding
import com.example.giao_dien.utils.ImageStorageManager
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

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvBackgrounds.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupClickListeners() {
        binding.viewDismiss.setOnClickListener { finish() }
    }

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
                                InterAdsUtils.showSplashAds(
                                    activity = this@ChooseBackgroundActivity,
                                    lifecycleOwner = this@ChooseBackgroundActivity,
                                    idAds = AdConfig.remoteInterstitialId,
                                    adPlacement = AdPlacement.INTER_SPLASH, // Hoặc định nghĩa cờ riêng nếu cần
                                    isEnable = FirebaseConfigManager.getInstance().adConfig.interSplash,
                                    action = {
                                        finish()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
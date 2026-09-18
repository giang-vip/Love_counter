package com.example.giao_dien.ui.memory

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giao_dien.ads.AdConfig
import com.example.giao_dien.ads.AdPlacement
import com.example.giao_dien.ads.FirebaseConfigManager
import com.example.giao_dien.ads.InterAdsUtils
import com.example.giao_dien.data.repository.MemoryRepositoryImpl
import com.example.giao_dien.databinding.ActivityAddMemoryBinding
import com.example.giao_dien.databinding.DialogSelectPhotoBinding
import com.example.giao_dien.utils.DatePickerDialogHelper
import com.example.giao_dien.utils.ImageStorageManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddMemoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMemoryBinding

    private val viewModel: AddMemoryViewModel by viewModels {
        val repository = MemoryRepositoryImpl(applicationContext)
        AddMemoryViewModel.Factory(repository)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = "memory_${System.currentTimeMillis()}.png"
            val localUri = ImageStorageManager.saveImageToInternalStorage(this, it, fileName)
            if (localUri != null) {
                viewModel.setImageUri(localUri.toString())
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val fileName = "memory_${System.currentTimeMillis()}.png"
            val folder = File(filesDir, "memories").apply { if (!exists()) mkdirs() }
            val file = File(folder, fileName)
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            viewModel.setImageUri(Uri.fromFile(file).toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddMemoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardAddPhoto.setOnClickListener { showPhotoOptionDialog() }
        binding.tvTapToAdd.setOnClickListener { showPhotoOptionDialog() }

        binding.layoutSelectDate.setOnClickListener {
            DatePickerDialogHelper.showCustomDatePickerDialog(
                this,
                binding.tvMemoryDate.text.toString()
            ) { dateStr ->
                binding.tvMemoryDate.text = dateStr
                viewModel.setDate(dateStr)
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveMemory(binding.edtMemoryTitle.text.toString())
        }
    }

    private fun showPhotoOptionDialog() {
        val dialogBinding = DialogSelectPhotoBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnTakePhoto.setOnClickListener {
            dialog.dismiss()
            cameraLauncher.launch(null)
        }

        dialogBinding.btnUploadPhoto.setOnClickListener {
            dialog.dismiss()
            galleryLauncher.launch("image/*")
        }

        dialog.show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.imageUriState.collect { uriStr ->
                        if (!uriStr.isNullOrEmpty()) {
                            binding.imgMemoryPreview.visibility = View.VISIBLE
                            binding.icPlusPhoto.visibility = View.GONE
                            try {
                                binding.imgMemoryPreview.setImageURI(Uri.parse(uriStr))
                            } catch (e: Exception) {
                                // Fallback
                            }
                        } else {
                            binding.imgMemoryPreview.visibility = View.GONE
                            binding.icPlusPhoto.visibility = View.VISIBLE
                        }
                    }
                }

                launch {
                    viewModel.dateState.collect { dateStr ->
                        if (dateStr.isNotEmpty()) {
                            binding.tvMemoryDate.text = dateStr
                        }
                    }
                }

                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is AddMemoryEvent.SavedSuccessfully -> {
                                InterAdsUtils.showSplashAds(
                                    activity = this@AddMemoryActivity,
                                    lifecycleOwner = this@AddMemoryActivity,
                                    idAds = AdConfig.remoteInterstitialId,
                                    adPlacement = AdPlacement.INTER_SPLASH,
                                    isEnable = FirebaseConfigManager.getInstance().adConfig.interSplash,
                                    action = { finish() }
                                )
                            }
                            is AddMemoryEvent.ShowToast -> {
                                Toast.makeText(this@AddMemoryActivity, getString(event.messageRes), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.giao_dien.ui.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giao_dien.R
import com.example.giao_dien.ads.NativeAdManager
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.repository.AppRepositoryImpl
import com.example.giao_dien.databinding.ActivityPermissionBinding
import kotlinx.coroutines.launch

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private val nativeAdManager = NativeAdManager()

    private val viewModel: PermissionViewModel by viewModels {
        val prefs = AppPreferences(applicationContext)
        val repository = AppRepositoryImpl(prefs)
        PermissionViewModel.Factory(repository)
    }

    // Nhận kết quả sau khi hệ thống Android hỏi quyền
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val cameraGranted = permissions[Manifest.permission.CAMERA] == true
            val photoGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
            } else {
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            }

            val allGranted = cameraGranted && photoGranted
            viewModel.updatePermissionResult(allGranted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
        observeViewModel()

        // Tải Native Ad dưới đuôi màn hình Permission
        nativeAdManager.loadNativeAd(this, binding.nativeAdContainer)
    }

    override fun onResume() {
        super.onResume()
        // Kiểm tra thực tế quyền từ hệ thống Android
        checkActualSystemPermissions()
    }

    private fun setupClickListeners() {
        // Bấm vào cả thẻ "Allow access" hoặc bấm vào nút Switch
        val onPermissionClick = {
            if (!viewModel.uiState.value.isPermissionGranted) {
                binding.switchPermission.isChecked = false
                showPermissionDialog()
            }
        }

        binding.layoutSwitch.setOnClickListener { onPermissionClick() }
        binding.switchPermission.setOnClickListener { onPermissionClick() }

        binding.tvContinue.setOnClickListener {
            viewModel.onContinueClicked()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_dialog_title))
            .setMessage(getString(R.string.permission_dialog_message))
            .setNegativeButton(getString(R.string.permission_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
                binding.switchPermission.isChecked = viewModel.uiState.value.isPermissionGranted
            }
            .setPositiveButton(getString(R.string.permission_dialog_confirm)) { _, _ ->
                requestPermissions()
            }
            .setCancelable(false)
            .show()
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissions.isEmpty()) {
            viewModel.updatePermissionResult(true)
            return
        }

        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun checkActualSystemPermissions() {
        val cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val photoGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        val allGranted = cameraGranted && photoGranted
        viewModel.updatePermissionResult(allGranted)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.switchPermission.isChecked = state.isPermissionGranted
                    }
                }

                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is PermissionEvent.FinishPermissionScreen -> {
                                val intent = Intent(this@PermissionActivity, com.example.giao_dien.ui.home.HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        nativeAdManager.destroyAd()
        super.onDestroy()
    }
}
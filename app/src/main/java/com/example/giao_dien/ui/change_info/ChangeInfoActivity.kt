package com.example.giao_dien.ui.change_info

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giao_dien.R
import com.example.giao_dien.ads.InterstitialAdManager
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.repository.CoupleRepositoryImpl
import com.example.giao_dien.databinding.ActivityChangeInfoBinding
import com.example.giao_dien.databinding.DialogSelectPhotoBinding
import com.example.giao_dien.domain.model.GenderType
import com.example.giao_dien.utils.DatePickerDialogHelper
import com.example.giao_dien.utils.ImageStorageManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ChangeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeInfoBinding
    private var isMale: Boolean = true

    private val viewModel: ChangeInfoViewModel by viewModels {
        val repository = CoupleRepositoryImpl(applicationContext)
        ChangeInfoViewModel.Factory(isMale, repository)
    }

    // Launchers cho Chụp ảnh & Chọn ảnh thư viện
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = if (isMale) "male_avatar.png" else "female_avatar.png"
            val localUri = ImageStorageManager.saveImageToInternalStorage(this, it, fileName)
            if (localUri != null) {
                viewModel.updateAvatarUri(localUri.toString())
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val fileName = if (isMale) "male_avatar.png" else "female_avatar.png"
            val folder = File(filesDir, "avatars").apply { if (!exists()) mkdirs() }
            val file = File(folder, fileName)
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            viewModel.updateAvatarUri(Uri.fromFile(file).toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        isMale = intent.getBooleanExtra(EXTRA_IS_MALE, true)

        binding = ActivityChangeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.cardInformation) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tải trước Interstitial Ad khi mở màn hình
        InterstitialAdManager.getInstance().loadAd(this)

        setupGenderSpinner()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupGenderSpinner() {
        val genders = arrayOf(
            getString(R.string.gender_male),
            getString(R.string.gender_female),
            getString(R.string.gender_other)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        binding.spGender.adapter = adapter

        binding.spGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGender = when (position) {
                    0 -> GenderType.MALE
                    1 -> GenderType.FEMALE
                    else -> GenderType.OTHER
                }
                viewModel.updateGender(selectedGender)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupClickListeners() {
        binding.btnSettings.setOnClickListener { finish() }

        // Bấm đổi avatar -> Mở dialog
        val onAvatarClick = { showPhotoOptionDialog() }
        binding.imgAvatar.setOnClickListener { onAvatarClick() }
        binding.tvChangePhoto.setOnClickListener { onAvatarClick() }

        // Bấm chọn ngày sinh
        binding.layoutBirthday.setOnClickListener { showDatePickerDialog() }

        // Bấm Save
        binding.btnSave.setOnClickListener {
            val inputName = binding.edtName.text.toString().trim()
            if (inputName.isNotEmpty()) {
                viewModel.updateName(inputName)
            }

            val inputBirthday = binding.tvBirthday.text.toString().trim()
            if (inputBirthday.isNotEmpty()) {
                viewModel.updateBirthday(inputBirthday)
            }

            viewModel.save()
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

    private fun showDatePickerDialog() {
        DatePickerDialogHelper.showCustomDatePickerDialog(
            this,
            binding.tvBirthday.text.toString()
        ) { dateStr ->
            binding.tvBirthday.setText(dateStr)
            viewModel.updateBirthday(dateStr)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.personState.collect { person ->
                        if (binding.edtName.text.toString() != person.name) {
                            binding.edtName.setText(person.name)
                        }

                        if (binding.tvBirthday.text.toString() != person.birthday) {
                            binding.tvBirthday.setText(person.birthday)
                        }

                        val genderIndex = when (person.gender) {
                            GenderType.MALE -> 0
                            GenderType.FEMALE -> 1
                            GenderType.OTHER -> 2
                        }
                        binding.spGender.setSelection(genderIndex)

                        if (!person.avatarUri.isNullOrEmpty()) {
                            try {
                                binding.imgAvatar.setImageURI(Uri.parse(person.avatarUri))
                            } catch (e: Exception) {
                                // Fallback
                            }
                        } else {
                            val defaultRes = if (isMale) R.drawable.img_avata_male else R.drawable.img_avata_female
                            binding.imgAvatar.setImageResource(defaultRes)
                        }
                    }
                }

                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is ChangeInfoEvent.SavedSuccessfully -> {
                                InterstitialAdManager.getInstance().showAd(this@ChangeInfoActivity) {
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_IS_MALE = "extra_is_male"
    }

}


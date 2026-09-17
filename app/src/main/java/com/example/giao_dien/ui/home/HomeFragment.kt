package com.example.giao_dien.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giao_dien.R
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.repository.CoupleRepositoryImpl
import com.example.giao_dien.databinding.FragmentHomeBinding
import com.example.giao_dien.domain.model.GenderType
import com.example.giao_dien.ui.background.ChooseBackgroundActivity
import com.example.giao_dien.ui.change_info.ChangeInfoActivity
import com.example.giao_dien.ui.setting.SettingActivity
import com.example.giao_dien.ui.start_date.StartDateActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val prefs = AppPreferences(requireContext().applicationContext)
        val repository = CoupleRepositoryImpl(requireContext().applicationContext, prefs)
        HomeViewModel.Factory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHomeData()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val coupleInfo = state.coupleInfo ?: return@collect

                    // Background Top
                    val backgroundUri = state.backgroundUri
                    if (!backgroundUri.isNullOrEmpty()) {
                        if (backgroundUri.startsWith("drawable://")) {
                            val resId = backgroundUri.removePrefix("drawable://").toIntOrNull()
                            if (resId != null) {
                                binding.imgTopBackground.setImageResource(resId)
                            }
                        } else {
                            try {
                                binding.imgTopBackground.setImageURI(Uri.parse(backgroundUri))
                            } catch (e: Exception) {
                                // Fallback
                            }
                        }
                    }

                    // Nam Info & Gender Icon Động
                    val maleAge = calculateAge(coupleInfo.maleInfo.birthday)
                    binding.tvNameMale.text = coupleInfo.maleInfo.name
                    binding.tvAgeMale.text = " $maleAge"
                    binding.tvAgeMale.setCompoundDrawablesWithIntrinsicBounds(
                        getGenderDrawable(coupleInfo.maleInfo.gender), 0, 0, 0
                    )
                    if (!coupleInfo.maleInfo.avatarUri.isNullOrEmpty()) {
                        try {
                            binding.imgAvatarMale.setImageURI(Uri.parse(coupleInfo.maleInfo.avatarUri))
                        } catch (e: Exception) {
                            binding.imgAvatarMale.setImageResource(R.drawable.img_avata_male)
                        }
                    } else {
                        binding.imgAvatarMale.setImageResource(R.drawable.img_avata_male)
                    }

                    // Nữ Info & Gender Icon Động
                    val femaleAge = calculateAge(coupleInfo.femaleInfo.birthday)
                    binding.tvNameFemale.text = coupleInfo.femaleInfo.name
                    binding.tvAgeFemale.text = " $femaleAge"
                    binding.tvAgeFemale.setCompoundDrawablesWithIntrinsicBounds(
                        getGenderDrawable(coupleInfo.femaleInfo.gender), 0, 0, 0
                    )
                    if (!coupleInfo.femaleInfo.avatarUri.isNullOrEmpty()) {
                        try {
                            binding.imgAvatarFemale.setImageURI(Uri.parse(coupleInfo.femaleInfo.avatarUri))
                        } catch (e: Exception) {
                            binding.imgAvatarFemale.setImageResource(R.drawable.img_avata_female)
                        }
                    } else {
                        binding.imgAvatarFemale.setImageResource(R.drawable.img_avata_female)
                    }

                    // Số ngày yêu (Mặc định 0 Day)
                    binding.tvDaysCount.text = "${state.loveDaysCount}\nDay"
                }
            }
        }
    }

    private fun getGenderDrawable(gender: GenderType): Int {
        return when (gender) {
            GenderType.MALE -> R.drawable.ic_male
            GenderType.FEMALE -> R.drawable.ic_female
            GenderType.OTHER -> R.drawable.ic_other
        }
    }

    private fun calculateAge(birthdayStr: String?): Int {
        if (birthdayStr.isNullOrBlank()) return 26
        return try {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val birthDate = sdf.parse(birthdayStr) ?: return 26
            val birthCal = java.util.Calendar.getInstance().apply { time = birthDate }
            val todayCal = java.util.Calendar.getInstance()
            var age = todayCal.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR)
            if (todayCal.get(java.util.Calendar.DAY_OF_YEAR) < birthCal.get(java.util.Calendar.DAY_OF_YEAR)) {
                age--
            }
            if (age in 0..120) age else 26
        } catch (e: Exception) {
            26
        }
    }

    private fun setupClickListeners() {
        binding.btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

        binding.imgAvatarMale.setOnClickListener { openChangeInfo(isMale = true) }
        binding.tvNameMale.setOnClickListener { openChangeInfo(isMale = true) }

        binding.imgAvatarFemale.setOnClickListener { openChangeInfo(isMale = false) }
        binding.tvNameFemale.setOnClickListener { openChangeInfo(isMale = false) }

        binding.layoutHeartCenter.setOnClickListener {
            val intent = Intent(requireContext(), StartDateActivity::class.java)
            startActivity(intent)
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent(requireContext(), ChooseBackgroundActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openChangeInfo(isMale: Boolean) {
        val intent = Intent(requireContext(), ChangeInfoActivity::class.java).apply {
            putExtra(ChangeInfoActivity.EXTRA_IS_MALE, isMale)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
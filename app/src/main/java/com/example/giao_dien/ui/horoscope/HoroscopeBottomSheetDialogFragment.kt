package com.example.giao_dien.ui.horoscope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.giao_dien.databinding.LayoutBottomSheetHoroscopeBinding
import com.example.giao_dien.domain.model.HoroscopeItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HoroscopeBottomSheetDialogFragment(
    private val items: List<HoroscopeItem>,
    private val onSelected: (HoroscopeItem) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: LayoutBottomSheetHoroscopeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutBottomSheetHoroscopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = HoroscopeAdapter { item ->
            onSelected(item)
            dismiss()
        }
        binding.rvHoroscopes.adapter = adapter
        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HoroscopeBottomSheetDialogFragment"
    }
}
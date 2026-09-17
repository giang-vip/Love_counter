package com.example.giao_dien.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.giao_dien.databinding.FragmentLoveTestBinding
import com.example.giao_dien.ui.horoscope.HoroscopeTestActivity
import com.example.giao_dien.ui.love_test.NameTestActivity

class LoveTestFragment : Fragment() {

    private var _binding: FragmentLoveTestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoveTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardNameTest.setOnClickListener {
            val intent = Intent(requireContext(), NameTestActivity::class.java)
            startActivity(intent)
        }

        binding.cardHoroscopeTest.setOnClickListener {
            val intent = Intent(requireContext(), HoroscopeTestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
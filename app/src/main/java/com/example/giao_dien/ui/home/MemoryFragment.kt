package com.example.giao_dien.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giao_dien.data.repository.MemoryRepositoryImpl
import com.example.giao_dien.databinding.FragmentMemoryBinding
import com.example.giao_dien.ui.memory.AddMemoryActivity
import com.example.giao_dien.ui.memory.MemoryAdapter
import kotlinx.coroutines.launch

class MemoryFragment : Fragment() {

    private var _binding: FragmentMemoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MemoryAdapter

    private val viewModel: MemoryViewModel by viewModels {
        val repository = MemoryRepositoryImpl(requireContext().applicationContext)
        MemoryViewModel.Factory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MemoryAdapter()
        binding.rvMemoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMemoryList.adapter = adapter
    }

    private fun setupClickListeners() {
        val onAddClick = {
            val intent = Intent(requireContext(), AddMemoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnEmptyAdd.setOnClickListener { onAddClick() }
        binding.btnFabAdd.setOnClickListener { onAddClick() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.memoriesState.collect { list ->
                    if (list.isEmpty()) {
                        binding.layoutEmptyState.visibility = View.VISIBLE
                        binding.rvMemoryList.visibility = View.GONE
                        binding.btnFabAdd.visibility = View.GONE
                    } else {
                        binding.layoutEmptyState.visibility = View.GONE
                        binding.rvMemoryList.visibility = View.VISIBLE
                        binding.btnFabAdd.visibility = View.VISIBLE
                        adapter.submitList(list)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.giao_dien.ui.memory

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.giao_dien.R
import com.example.giao_dien.databinding.ItemMemoryBinding
import com.example.giao_dien.domain.model.MemoryInfo

class MemoryAdapter(
    private val onItemClick: (MemoryInfo) -> Unit = {}
) : ListAdapter<MemoryInfo, MemoryAdapter.MemoryViewHolder>(DIFF_CALLBACK) {

    class MemoryViewHolder(
        val binding: ItemMemoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val binding = ItemMemoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvMemoryTitle.text = item.title
            tvMemoryDate.text = item.date

            if (!item.imageUri.isNullOrEmpty()) {
                try {
                    imgMemory.setImageURI(Uri.parse(item.imageUri))
                } catch (e: Exception) {
                    imgMemory.setImageResource(R.drawable.backgroud_home1)
                }
            } else {
                imgMemory.setImageResource(R.drawable.backgroud_home1)
            }

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemoryInfo>() {
            override fun areItemsTheSame(oldItem: MemoryInfo, newItem: MemoryInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MemoryInfo, newItem: MemoryInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}
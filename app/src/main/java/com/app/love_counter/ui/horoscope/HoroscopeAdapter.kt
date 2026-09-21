package com.app.love_counter.ui.horoscope

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.love_counter.databinding.ItemHoroscopeBinding
import com.app.love_counter.domain.model.HoroscopeItem

class HoroscopeAdapter(
    private val onItemClick: (HoroscopeItem) -> Unit
) : ListAdapter<HoroscopeItem, HoroscopeAdapter.HoroscopeViewHolder>(DIFF_CALLBACK) {

    class HoroscopeViewHolder(
        val binding: ItemHoroscopeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        val binding = ItemHoroscopeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HoroscopeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvHoroscopeName.text = item.name
            imgHoroscopeIcon.setImageResource(item.iconRes)

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HoroscopeItem>() {
            override fun areItemsTheSame(oldItem: HoroscopeItem, newItem: HoroscopeItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HoroscopeItem, newItem: HoroscopeItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
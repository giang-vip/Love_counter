package com.example.giao_dien.ui.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.giao_dien.R
import com.example.giao_dien.databinding.ItemLanguageBinding
import com.example.giao_dien.domain.model.Language

class LanguageAdapter(
    private val onLanguageClick: (Language) -> Unit
) : ListAdapter<Language, LanguageAdapter.LanguageViewHolder>(
    DIFF_CALLBACK
) {

    class LanguageViewHolder(
        val binding: ItemLanguageBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        position: Int
    ) {
        val language = getItem(position)

        with(holder.binding) {
            tvLanguage.setText(language.nameRes)
            ivFlag.setImageResource(language.flagRes)

            if (language.isSelected) {
                root.setBackgroundResource(R.drawable.bg_language_item_selected)
            } else {
                root.setBackgroundResource(R.drawable.bg_language_item)
            }

            root.setOnClickListener {
                val currentPosition = holder.adapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                val clickedLanguage = getItem(currentPosition)
                if (clickedLanguage.isSelected) return@setOnClickListener

                val newList = currentList.map { item ->
                    item.copy(isSelected = item.code == clickedLanguage.code)
                }

                submitList(newList)
                onLanguageClick(clickedLanguage)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Language>() {
            override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
                return oldItem.code == newItem.code
            }

            override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
                return oldItem == newItem
            }
        }
    }
}
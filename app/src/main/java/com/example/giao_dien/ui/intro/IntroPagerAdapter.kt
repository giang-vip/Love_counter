package com.example.giao_dien.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giao_dien.databinding.ItemIntroPageBinding
import com.example.giao_dien.domain.model.IntroPage

class IntroPagerAdapter(
    private val pages: List<IntroPage>
) : RecyclerView.Adapter<IntroPagerAdapter.IntroViewHolder>() {

    class IntroViewHolder(
        val binding: ItemIntroPageBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntroViewHolder {
        val binding = ItemIntroPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntroViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: IntroViewHolder,
        position: Int
    ) {
        val page = pages[position]
        holder.binding.ivIntro.setImageResource(page.imageRes)
    }

    override fun getItemCount(): Int = pages.size
}
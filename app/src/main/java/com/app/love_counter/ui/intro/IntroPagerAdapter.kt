package com.app.love_counter.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.love_counter.databinding.ItemIntroPageBinding
import com.app.love_counter.domain.model.IntroPage

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
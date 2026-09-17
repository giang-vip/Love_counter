package com.example.giao_dien.ui.background

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giao_dien.databinding.ItemBackgroundBinding

data class BackgroundItem(
    val isAddButton: Boolean = false,
    val drawableRes: Int? = null,
    val uriString: String? = null
)

class BackgroundAdapter(
    private val items: List<BackgroundItem>,
    private val onItemClick: (BackgroundItem) -> Unit
) : RecyclerView.Adapter<BackgroundAdapter.BackgroundViewHolder>() {

    class BackgroundViewHolder(
        val binding: ItemBackgroundBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BackgroundViewHolder {
        val binding = ItemBackgroundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BackgroundViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BackgroundViewHolder,
        position: Int
    ) {
        val item = items[position]
        with(holder.binding) {
            if (item.isAddButton) {
                layoutPlus.visibility = View.VISIBLE
                imgBackground.visibility = View.GONE
            } else {
                layoutPlus.visibility = View.GONE
                imgBackground.visibility = View.VISIBLE

                if (item.uriString != null) {
                    try {
                        imgBackground.setImageURI(Uri.parse(item.uriString))
                    } catch (e: Exception) {
                        // Fallback
                    }
                } else if (item.drawableRes != null) {
                    imgBackground.setImageResource(item.drawableRes)
                }
            }

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
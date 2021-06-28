package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.ItemAdBinding
import ru.netology.nmedia.model.AdModel
import ru.netology.nmedia.view.load

class AdViewHolder(
    private val binding: ItemAdBinding,
    private val listener: PostAdapterClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: AdModel) {
        binding.apply {
            image.load("${BuildConfig.BASE_URL}/media/${ad.picture}")
            root.setOnClickListener{
                listener.onAdClicked(ad)
            }
        }
    }
}
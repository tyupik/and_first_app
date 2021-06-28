package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ItemAdBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.databinding.ItemPostBinding
import ru.netology.nmedia.model.AdModel
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.PostModel


class PostAdapter(
    private val listener: PostAdapterClickListener,
    private val url: String
) : PagingDataAdapter<FeedModel, RecyclerView.ViewHolder>(PostDiffItemCallBack) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is AdModel -> R.layout.item_ad
            is PostModel -> R.layout.item_post
            null -> error("Unknown type at $position")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.item_ad -> {
                val binding =
                    ItemAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding, listener)
            }
            R.layout.item_post -> {
                PostViewHolder(
                    ItemPostBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    listener,
                    url
                )
            }
            else -> error("Unknown view type $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val item = getItem(position) as PostModel
                holder.bind(item.post)
            }
            is AdViewHolder -> {
                val item = getItem(position) as AdModel
                holder.bind(item)
            }
        }
    }

    object PostDiffItemCallBack : DiffUtil.ItemCallback<FeedModel>() {
        override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            if (oldItem.javaClass != newItem.javaClass) {
                return false
            }
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean =
            oldItem == newItem
    }
}
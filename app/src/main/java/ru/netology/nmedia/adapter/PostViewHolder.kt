package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Post
import ru.netology.nmedia.databinding.ItemPostBinding
import kotlin.math.floor

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onPostLiked: OnLikeListener,
    private val onPostShared: OnShareListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            authorTv.text = post.author
            publishedTv.text = post.published
            textTv.text = post.content
            like.setImageResource(
                if (post.likedByMe) ru.netology.nmedia.R.drawable.like_red else ru.netology.nmedia.R.drawable.empty_like
            )
            binding.likeCount.text = setRoundCount(post.likeCount)
            binding.shareCount.text = setRoundCount(post.shareCount)

            like.setOnClickListener {
                onPostLiked(post)
            }
            share.setOnClickListener {
                onPostShared(post)
            }
        }
    }

    private fun setRoundCount(value: Int): String {
        return when (value) {
            0 -> ""
            in 1..999 -> value.toString()
            in 1000..1099 -> "1K"
            in 1100..9999 -> (floor(value.toDouble() / 1000 * 10f) / 10f).toString() + "K"
            in 10000..999_999 -> floor(value.toDouble() / 1000).toInt().toString() + "K"
            else -> (floor(value.toDouble() / 1_000_000 * 10f) / 10f).toString() + "M"
        }
    }
}
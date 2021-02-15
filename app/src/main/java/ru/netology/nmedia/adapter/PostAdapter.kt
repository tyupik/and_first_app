package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.Post
import ru.netology.nmedia.databinding.ItemPostBinding
import kotlin.math.floor

typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit


class PostAdapter(
    val onPostLiked: OnLikeListener,
    val onPostShared: OnShareListener,

) : ListAdapter<Post, PostViewHolder>(PostDiffItemCallBack) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            binding.like.setOnClickListener {
                onPostLiked(getItem(adapterPosition))
            }
            binding.share.setOnClickListener {
                onPostShared(getItem(adapterPosition))
            }
        }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        with(holder.binding) {
            authorTv.text = post.author
            publishedTv.text = post.published
            textTv.text = post.content
            like.setImageResource(
                if (post.likedByMe) ru.netology.nmedia.R.drawable.like_red else ru.netology.nmedia.R.drawable.empty_like
            )
            likeCount.text = setRoundCount(post.likeCount)
            shareCount.text = setRoundCount(post.shareCount)

        }
    }


    object PostDiffItemCallBack : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
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
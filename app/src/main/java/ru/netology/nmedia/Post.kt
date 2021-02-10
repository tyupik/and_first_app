package ru.netology.nmedia

import android.widget.ImageView

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: ImageView? = null,
    val content: String,
    val published: String,
    val likeByMe: Boolean = false,
    val likeCount: Int = 0,
    val shareCount: Int = 0
)
